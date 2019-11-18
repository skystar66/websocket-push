package com.socket.server.template.impl;

import com.socket.server.factory.WsFactory;
import com.socket.server.factory.WsHolder;
import com.socket.server.future.WsCallBack;
import com.socket.server.future.WsCallBackAdapter;
import com.socket.server.future.WsFuture;
import com.socket.server.manager.MsgManager;
import com.socket.server.model.WsRequest;
import com.socket.server.model.WsResponse;
import com.socket.server.template.WsTemplete;
import com.socket.server.util.StockHashUtil;
import com.socket.server.util.SubscribeBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultTemplete implements WsTemplete {

    @Autowired
    WsFactory wsFactory;


    @Override
    public WsFuture sendAsynByFuture(WsRequest request) {

        //随机获取连接
        WsHolder wsHolder = wsFactory.getWsHolder();

        if (wsHolder == null) {
            log.error(">>>>> 获取连接失败");
        }

        return MsgManager.sendFuture(wsHolder, request);
    }

    @Override
    public void sendByCallBack(WsRequest request, WsCallBack callBack) {

        //随机获取连接
        WsHolder wsHolder = wsFactory.getWsHolder();

        if (wsHolder == null) {
            log.error(">>>>> 获取连接失败");
        }

        MsgManager.sendCallBack(wsHolder, request, callBack);
    }


    @Override
    public void subscribe(String stockId) {
        int index = StockHashUtil.getHashIndex(stockId, 3);
        WsHolder wsHolder = wsFactory.getHolder(index);
        if (null == wsHolder) {
            log.error(">>>>> 订阅失败，连接丢失");
            return;
        }
        //加入锁重入 幂等性
        synchronized (wsHolder) {
            if (wsHolder.isSubId(stockId)) {
                return;
            }
            WsRequest wsRequest = new WsRequest(SubscribeBiz.subMsgBody(stockId));
            MsgManager.sendCallBack(wsHolder, wsRequest, new WsCallBackAdapter() {
                @Override
                public void onMsg(WsResponse response) {
                    log.info(">>>>>> 订阅成功，收到返回结果： {}", response);
                    if (!wsHolder.isSubId(stockId)) {
                        wsHolder.addSubId(stockId);
                    }
                }
            });
        }
    }

    @Override
    public void canalSubscribe(String stockId) {
        int index = StockHashUtil.getHashIndex(stockId, 3);
        WsHolder wsHolder = wsFactory.getHolder(index);
        if (null == wsHolder) {
            log.error(">>>>> 取消订阅失败，连接丢失");
            return;
        }
        //加入锁重入 幂等性
        synchronized (wsHolder) {
            if (wsHolder.isSubId(stockId)) {
                return;
            }
            WsRequest wsRequest = new WsRequest(SubscribeBiz.canalSubMsgBody(stockId));
            MsgManager.sendCallBack(wsHolder, wsRequest, new WsCallBackAdapter() {
                @Override
                public void onMsg(WsResponse response) {
                    log.info(">>>>>> 取消订阅成功，收到返回结果： {}", response);
                    if (wsHolder.isSubId(stockId)) {
                        wsHolder.delSubId(stockId);
                    }
                }
            });
        }


    }
}
