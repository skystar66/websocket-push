package com.socket.push.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.socket.push.core.PushManagerBiz;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * WebSocket是线程安全的，有用户连接时就会创建一个新的端点实例
 * 主要用来接收客户端发送的 订阅通知
 *
 * @author xuliang
 * @date : 2019-10-23
 */
@ServerEndpoint(value = "/websocket/server")
@Component
public class WebSocketServer {

    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private PushManagerBiz pushBiz = null;

    @OnOpen
    public void onOpen(Session session) {

        /*
         * issue fix:最大空闲
         * Idle timeout expired: 300000/300000 ms
         * 具体设置可以参考 WebSocketPolicy
         */
        session.setMaxIdleTimeout(300000);
        logger.info("建立新的连接:clientId={}", session.getId());
        pushBiz().addSession(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("连接关闭:closeReason={},sessionId={}", closeReason, session.getId());
        // 断开
        pushBiz().removeSession(session.getId());
    }

    @OnMessage
    public void onMessage(String msg, Session session) {

        logger.info("收到消息:msg={},clientId={}", msg, session.getId());

        try {

            JSONObject json = JSON.parseObject(msg);
            String ids = json.getString("stockIds");
            Integer event = json.getInteger("event");

            if (ids == null || event == null) {
                logger.warn("非订阅相关消息,忽略:{}", msg);
                return;
            } else {
                String[] ts = ids.split(",");
                for (String id : ts) {
                    if (StringUtils.isNoneBlank(id)) {
                        pushBiz().addSubAndUnSub(session.getId(), id, event);
                    }
                }
            }

        } catch (Exception e) {

            logger.error("收到消息，处理异常:{}", msg);

            logger.error(e.getMessage(), e);
        }
    }

    /**
     * #issue fix: 空闲超时,可以设置session.setMaxIdleTimeout
     * java.util.concurrent.TimeoutException:Idle timeout expired: 300000/300000 ms
     * <p>
     * #同时也会触发:
     * 连接关闭:closeReason=CloseReason[1001,Idle Timeout]
     *
     * @param session
     * @param ex
     */
    @OnError
    public void onError(Session session, Throwable ex) {

        logger.info("发生错误:clientId={}", session.getId());

        if (ex != null) {
            logger.error(ex.getMessage(), ex);
        }

        // 断开
        pushBiz().removeSession(session.getId());
    }

    private PushManagerBiz pushBiz() {

        if (pushBiz == null) {
            pushBiz = SpringUtil.getBean(PushManagerBiz.class);
        }

        return pushBiz;
    }


    /**
     * 客户端数量
     * */



}