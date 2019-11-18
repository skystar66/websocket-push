package com.socket.server.model;

import lombok.Data;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class WsFutureModel {


    //requestId
    private long id;

    private WsRequest request;

    /**
     * 超时时间,默认 @see WsConstants.DEFAULT_TIMEOUT
     */
    private int timeout;

    private final Lock lock = new ReentrantLock();

    private final Condition done = lock.newCondition();

    private long start = System.currentTimeMillis();

}
