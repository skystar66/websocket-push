package com.socket.server.config;

import com.socket.server.annotations.MMPushHandler;
import com.socket.server.factory.WsFactory;
import com.socket.server.future.WsCallBack;
import com.socket.server.manager.PushManager;
import com.socket.server.model.WsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;

/**
 * ws初始化上下文
 *
 * @author xuliang
 * @date:2019-10-22
 */
@Component
@Slf4j
public class WsContextInit implements ApplicationContextAware {


    @Autowired
    WsFactory wsFactory;

    @Autowired
    WsConfig wsConfig;

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @PostConstruct
    public void init() {

        //共享连接池
        initShardWsConnect();
        //共享推送
        initPushHandler();

    }

    /**
     * 初始化ws共享连接
     */

    public void initShardWsConnect() {
        int corePoolSize = wsConfig.getCorePoolSize();
        log.info(">>>>>>> 开始初始化共享连接池 corePoolSize：{} 个", corePoolSize);
        for (int i = 0; i < corePoolSize; i++) {
            WsContext wsContext = new WsContext();
            wsContext.setId(i);
            wsContext.setUrl(wsConfig.getWsUrl());
            wsFactory.addWsConnect(wsContext);
        }
        log.info(">>>>>> 共享连接池初始化成功！");
    }


    /**
     * 初始化推送连接池
     */

    public void initPushHandler() {
        Map<String, WsCallBack> map = applicationContext.getBeansOfType(WsCallBack.class);
        if (map == null || map.isEmpty()) {
            return;
        }
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            WsCallBack v = map.get(it.next());
            MMPushHandler mmPushHandler = v.getClass().getAnnotation(MMPushHandler.class);
            if (mmPushHandler != null) {
                PushManager.registPushCallBack(mmPushHandler.value(), v);
                log.info("添加MM-push处理器:{}", v);
            }
        }
    }


}
