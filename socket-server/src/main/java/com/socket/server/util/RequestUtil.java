package com.socket.server.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author :xuliang
 * @desc:requestId生成工具
 * @date:2019-10-22
 */
public class RequestUtil {


    //原子操作,考虑到多线程场景
    private static final AtomicLong INVOKE_ID = new AtomicLong(123);

    public static long newId() {
        return INVOKE_ID.getAndIncrement();
    }



}
