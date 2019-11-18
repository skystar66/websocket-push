package com.socket.server.manager;

import com.alibaba.fastjson.JSONObject;
import com.socket.server.factory.WsHolder;
import com.socket.server.future.WsCallBack;
import com.socket.server.future.WsFuture;
import com.socket.server.model.WsRequest;
import com.socket.server.model.WsResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author xuliang
 * @desc:收发消息 管理器
 * @date:2019-10-22
 */
@Slf4j
public class MsgManager {


    /**
     * 有消息时回调
     */
    public static void callBack(WsResponse wsResponse) {
        RegisterManager.recived(wsResponse);
    }

    /**
     * 发送消息 future
     */
    public static WsFuture sendFuture(WsHolder wsHolder, WsRequest wsRequest) {
        String msg = buildMsg(wsHolder, wsRequest);
        log.info(">>>>>> 发送消息 msg : {}", msg);
        wsHolder.getWebSocketClient().send(msg);
        WsFuture wsFuture = new WsFuture(wsRequest);
        //注册 future
        RegisterManager.registFuture(wsFuture);
        return wsFuture;
    }

    /**
     * 发送消息 callback
     */
    public static void sendCallBack(WsHolder wsHolder, WsRequest wsRequest, WsCallBack wsCallBack) {
        String msg = buildMsg(wsHolder, wsRequest);
        log.info(">>>>> 发送消息 msg : {}", msg);
        wsHolder.getWebSocketClient().send(msg);
        wsHolder.setLastActiveTime(new Date());
        RegisterManager.registCallBack(wsRequest.getRequestId(), wsCallBack);
    }


    private static String buildMsg(WsHolder wsHolder, WsRequest request) {
        JSONObject msg = new JSONObject();
        // issue fix:首字母大写
        msg.put("EptFlag", request.getEptFlag());
        msg.put("SeqNum", request.getRequestId());
        msg.put("MsgBody", request.getMsgBody());
        msg.put("bussType",10001);//订阅/取消订阅时 赋值，其他消息体不用赋值
        return msg.toJSONString();
    }

}
