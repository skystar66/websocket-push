package com.socket.provider.server;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/webSocketProvideServer")
@RestController
@Slf4j
public class WebSocketProvideServer {


    // 用来记录当前连接数的变量
    private static volatile int onlineCount = 0;

    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketProvideServer> webSocketSet = new CopyOnWriteArraySet<WebSocketProvideServer>();

    // 与某个客户端的连接会话，需要通过它来与客户端进行数据收发
    private Session session;

    @OnOpen
    public void onOpen(Session session) throws Exception {
        this.session = session;
        webSocketSet.add(this);
        log.info("Open a websocket. id={}", session.getId());
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("Close a websocket. ");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Receive a message from push: " + message);
        try {
            sendMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Error while websocket. ", error);
    }

    public void sendMessage(String message) throws Exception {

        JSONObject msg = JSONObject.parseObject(message);


        if (this.session.isOpen()) {
            this.session.getBasicRemote().sendText(msg.toJSONString());
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketProvideServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketProvideServer.onlineCount--;
    }


}
