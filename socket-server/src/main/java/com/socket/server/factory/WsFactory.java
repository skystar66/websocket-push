package com.socket.server.factory;

import com.socket.server.future.WsCallBackAdapter;
import com.socket.server.listener.ReConnectListener;
import com.socket.server.manager.MsgManager;
import com.socket.server.model.WsContext;
import com.socket.server.model.WsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xuliang
 * @desc: 连接工厂管理 1.添加和删除 2.自动重连
 * @date:2019-10-22
 */
@Component
@Slf4j
public class WsFactory {

    //共享连接
    public static final List<WsHolder> sharedConnectPool = new CopyOnWriteArrayList<>();

    @Autowired
    ReConnectListener reConnectListener;


    @PostConstruct
    public void init() {
        refreshConnect();
    }

    /**
     * 守护线程，间隔扫描，如果发现连接断开，重新连接
     */
    public void refreshConnect() {
        log.info(">>>>>> 刷新连接扫描");

        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (!CollectionUtils.isEmpty(sharedConnectPool)) {

                            for (WsHolder wsHolder : sharedConnectPool) {
                                if (!wsHolder.isRuning()) {
                                    //重连
                                    wsHolder.reConnect();
                                    //订阅重连
                                    if (reConnectListener != null) {
                                        reConnectListener.onReConnect(wsHolder);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error(">>>>>> 重连发生错误，errorMsg:{}", ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    try {
                        //重连间隔
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        //设置为守护线程
        th1.setDaemon(true);
        th1.setName("refresh-connect-thread");
        th1.start();
    }


    /**
     * 获取可用连接,随机获取，后期 可使用 负载均衡策略
     */
    public WsHolder getWsHolder() {
        Random r = new Random();
        return sharedConnectPool.get(r.nextInt(sharedConnectPool.size()));
    }

    /**
     * 获取连接
     */
    public WsHolder getHolder(int index) {
        return sharedConnectPool.get(index);
    }


    /**
     * 关闭连接
     */
    @PreDestroy
    public void close() {
        if (sharedConnectPool != null) {
            for (WsHolder wsch : sharedConnectPool) {
                if (wsch != null) {
                    log.info("关闭连接={}", wsch);
                    wsch.close();
                }
            }
        }
    }

    /**
     * 添加链接
     */
    public void addWsConnect(WsContext wsContext) {

        WsHolder wsHolder = new WsHolder(wsContext, new WsCallBackAdapter() {
            @Override
            public void onMsg(WsResponse response) {
                MsgManager.callBack(response);
            }
        });
        //连接
        wsHolder.createConnect();
        //添加到连接池
        sharedConnectPool.add(wsHolder);
    }


}
