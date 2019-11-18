package com.socket.client.config;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @Auther: xuliang
 * @Date: 2019/10/23 17:38
 * @Description: 配置websocket后台客户端
 */
@Slf4j
@Component
public class WebSocketConfig {


    @Autowired
    WsConfig wsConfig;

    @Bean
    public WebSocketClient webSocketClient() {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(wsConfig.getUrl()), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("[websocket] 连接成功");
                }

                @Override
                public void onMessage(String message) {
                    log.info("[websocket] 收到消息={}", message);

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("[websocket] 退出连接");
                }

                @Override
                public void onError(Exception ex) {
                    log.info("[websocket] 连接错误={}", ex.getMessage());
                }
            };
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}