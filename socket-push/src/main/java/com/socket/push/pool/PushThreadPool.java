package com.socket.push.pool;

import com.socket.push.util.NamedThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 推送push线程池
 *
 * @author xuliang
 * @date:2019-10-23
 */
@Component
public class PushThreadPool {

    //最大可用的CPU核数
    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    //线程最大的空闲存活时间，单位为秒
    public static final int KEEPALIVETIME = 60;
    //任务缓存队列长度
    public static final int BLOCKINGQUEUE_LENGTH = 500;

    //执行服务
    private ExecutorService executor = null;

    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(PROCESSORS * 2, PROCESSORS * 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(BLOCKINGQUEUE_LENGTH),
                new NamedThreadFactory("push", true));
    }


    public void submit(Runnable task) {
        executor.submit(task);
    }


}
