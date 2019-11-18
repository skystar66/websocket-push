package com.socket.server.config;

import com.socket.server.constants.WsConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ws")
@Data
public class WsConfig {

    /**
     * 请求超时时间,单位:毫秒,默认:10s
     */
    private int reqestTimeout = WsConstants.DEFAULT_TIMEOUT;

    /**
     * 连接个数,默认-1,表示不创建
     */
    private int corePoolSize;

    /**
     * mm-websocket地址
     */
    private String wsUrl;

}
