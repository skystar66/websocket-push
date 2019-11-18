package com.socket.push.handler;

import com.alibaba.fastjson.JSONObject;
import com.socket.push.constants.Constants;
import com.socket.push.core.PushManagerBiz;
import com.socket.push.pool.PushThreadPool;
import com.socket.server.annotations.MMPushHandler;
import com.socket.server.future.WsCallBackAdapter;
import com.socket.server.model.WsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订阅推送处理器
 *
 * @author xuliang
 * @date:2019-10-23
 */
@Component
@MMPushHandler(Constants.BUSSINES_TYPE_PUSH)
@Slf4j
public class MyMMPushHandler extends WsCallBackAdapter {
    @Autowired
    PushThreadPool pushThreadPool;
    @Autowired
    PushManagerBiz pushManagerBiz;


    @Override
    public void onMsg(WsResponse response) {
        //收到MM返回的消息
        if (response == null) {
            return;
        }
        pushThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                //多线程 用trycatch  捕捉，一面多线程dead掉
                try {
                    //获得结果
                    JSONObject jsonObject = response.getResult();
                    String stockId = "";
                    String msg = "";
                    if (null != jsonObject) {
                        stockId = jsonObject.getString("MsgBody");
                        msg = jsonObject.toJSONString();
                    }
                    pushManagerBiz.pushAll(stockId, msg);
                } catch (Exception ex) {
                    log.error(">>>>>> 推送客户端出现异常 response : {}", response);
                    ex.printStackTrace();
                }

            }
        });
    }
}
