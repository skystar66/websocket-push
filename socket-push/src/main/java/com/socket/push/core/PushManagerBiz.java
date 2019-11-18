package com.socket.push.core;

import com.socket.server.template.WsTemplete;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

/**
 * 推送业务处理 1，用户会话 2，订阅 3，推送消息功能
 *
 * @author xuliang
 * @date:2019-10-23
 */
@Component
@Slf4j
public class PushManagerBiz {

    @Autowired
    WsTemplete wsTemplete;

    /**
     * 用户session管理
     */
    public final static Map<String, Session> userSession = new ConcurrentHashMap<>();

    /**
     * 订阅该只股票的所有人
     */
    public final static Map<String, Set<String>> stockUser = new ConcurrentHashMap<>();


    /**
     * 记录空闲Session集合
     */
    private static CopyOnWriteArraySet<Session> idle = new CopyOnWriteArraySet<Session>();
    /**
     * 记录正在使用中Session集合，value为Future，表示使用情况
     */
    private static ConcurrentHashMap<Session, Future<Void>> busy = new ConcurrentHashMap<Session, Future<Void>>();


    /**
     * 推送给订阅者该只股票数据
     *
     * @param stockId 股票id
     * @param msg     该只股票的数据
     */
    public void pushAll(String stockId, String msg) {
        //获取订阅该只股票的所有人
        Set<String> userIds = stockUser.get(stockId);
        if (!CollectionUtils.isEmpty(userIds)) {
            for (String userId : userIds) {
                try {
                    //获得该用户的channel session
                    Session session = userSession.get(userId);
                    if (null != session) {
                        //异步发送出去
//                        session.getAsyncRemote().sendText(msg);
                        aysnSend(session, msg, 3000);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }


    /**
     * 使用session发送消息,后期可替换Redis
     */
    public static void aysnSend(Session session, String message, Integer timeout) throws InterruptedException {
        if (timeout < 0) { // timeout后放弃本次发送
            return;
        }
        // 判断session是否空闲，抢占式
        if (idle.remove(session)) {
            busy.put(session, session.getAsyncRemote().sendText(message));
        } else {
            // 若session当前不在idle集合，则去busy集合中查看session上次是否已经发送完毕，即采用惰性判断
            synchronized (busy) {
                if (busy.containsKey(session) && busy.get(session).isDone()) {
                    busy.remove(session);
                    idle.add(session);
                }
            }
            // 重试
            Thread.sleep(100);
            aysnSend(session, message, timeout - 100);
        }
    }


    /**
     * 订阅/取消订阅
     *
     * @param uid
     * @param stockId
     * @param type    0订阅/1取消订阅
     */
    public synchronized void addSubAndUnSub(String uid, String stockId, Integer type) {

        Set<String> userids = stockUser.get(stockId);
        if (type == 1) {
            //取消订阅
            if (stockUser != null) {
                if (!CollectionUtils.isEmpty(userids)) {
                    userids.remove(uid);
                    //判断改制股票是否还有订阅者
                    if (CollectionUtils.isEmpty(userids)) {
                        //像MM发送取消该只股票订阅的通知
                        wsTemplete.canalSubscribe(stockId);
                    }
                }
            }
        } else {
            //发起订阅
            if (CollectionUtils.isEmpty(userids)) {
                userids = new HashSet<>();
                userids.add(uid);
                //像MM发起首次订阅该只股票
                wsTemplete.subscribe(stockId);
                stockUser.put(stockId, userids);
            } else {
                //如果不是首次订阅，将该用户加入到订阅列表
                userids.add(uid);
            }
        }
    }

    public static void addSession(String uid, Session session) {
        userSession.put(uid, session);
        idle.add(session);
    }


    public synchronized void removeSession(String uid) {
        Session session = userSession.remove(uid);
        if (null != session) {
            idle.remove(session);
            busy.remove(session);
        }

    }


}
