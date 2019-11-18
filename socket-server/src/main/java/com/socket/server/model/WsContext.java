package com.socket.server.model;


import lombok.Data;

/**
 * ws上下文
 */
@Data
public class WsContext {


    private int id;

    /**
     * ws 连接地址
     */
    private String url;


}
