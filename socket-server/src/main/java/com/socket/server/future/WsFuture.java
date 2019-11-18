package com.socket.server.future;

import com.socket.server.enums.WsResponseCode;
import com.socket.server.model.WsFutureModel;
import com.socket.server.model.WsRequest;
import com.socket.server.model.WsResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : liangxl
 * @desc: 适合用于 http-ws 同步接收返回结果
 * @date : 2019-10-22
 */
@Slf4j
@Data
public class WsFuture {

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


    private volatile WsResponse response;

    public WsFuture(WsRequest request) {
        this.request = request;
        this.id = request.getRequestId();
        this.timeout = request.getTimeout();
    }

    /**
     * future 模式，在指定时间内，
     * 获取数据，如果 获取不到 或抛出异常，代表由于网络等其它问题超时
     */
    public WsResponse get() {
        if (!isComplate()) {
            long start = System.currentTimeMillis();
            //加锁，保证一致性
            lock.lock();
            try {
                while (!isComplate()) {
                    done.await(timeout, TimeUnit.MINUTES);
                    if (isComplate() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        if (!isComplate()) {
            log.info(">>>>>> 请求时间超时 ，{}", request);
            return new WsResponse(WsResponseCode.FAILURE);
        }
        WsResponse wsResponse = response;
        return wsResponse;
    }


    /**
     * 校验数据是否已经成功接收
     */
    public boolean isComplate() {
        return response != null;
    }

    /**
     * 接收相应数据
     */
    public void reciveResponse(WsResponse wsResponse) {
        lock.lock();
        try {
            this.response = wsResponse;
            if (done != null) {
                done.signal();
            }
        } catch (Exception ex) {
            log.error(">>>>>> recive response is error ！");
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
