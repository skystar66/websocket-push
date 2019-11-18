package com.socket.client.service;

public interface WebSocketService {


    /**
     * 群发
     *
     * @param message
     */
    void groupSending(String message);

    /**
     * 指定发送
     *
     * @param name
     * @param message
     */
    void appointSending(String name, String message);



    /**
     * 订阅
     *
     * @param message
     */
    void sub(String stockIds,Integer type);

    /**
     * 取消订阅
     *
     * @param name
     * @param message
     */
    void unSub(String stockIds);


}
