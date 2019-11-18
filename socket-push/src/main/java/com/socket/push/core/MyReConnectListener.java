package com.socket.push.core;

import com.socket.server.factory.WsHolder;
import com.socket.server.future.WsCallBackAdapter;
import com.socket.server.listener.ReConnectListener;
import com.socket.server.manager.MsgManager;
import com.socket.server.model.WsRequest;
import com.socket.server.model.WsResponse;
import com.socket.server.util.SubscribeBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 重连监听处理
 *
 * @author xuliang
 * @date : 2019-10-23
 */
@Component
@Slf4j
public class MyReConnectListener implements ReConnectListener {


    /**
     * 重连处理器，发生重连时  需要重新订阅MM数据
     */
    @Override
    public void onReConnect(WsHolder wsHolder) {

        //获取订阅集合
        Map<String, String> subMap = wsHolder.getSubmap();
        if (null == subMap) {
            log.info(">>>>>>> 没有订阅数据，不需要重新订阅！");
            return;
        }
        //重新订阅
        for (String stockId : subMap.keySet()) {
            WsRequest wsRequest = new WsRequest(SubscribeBiz.subMsgBody(stockId));
            MsgManager.sendCallBack(wsHolder, wsRequest, new WsCallBackAdapter() {
                @Override
                public void onMsg(WsResponse response) {
                    log.info(">>>>>> 重连后发起订阅成功，收到返回结果： {}", response);
                }
            });
        }


    }
}
