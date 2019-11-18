package com.socket.server.manager;


import com.alibaba.fastjson.JSONObject;
import com.socket.server.future.WsCallBack;
import com.socket.server.model.WsResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuliang
 * @desc：各类消息管理器 1，添加订阅 取消订阅
 */
@Slf4j
public class PushManager {


    private static final Map<String, WsCallBack> pushCallBack = new ConcurrentHashMap<>();

    /**
     * 注册 callback ，如行情推送
     */

    public static void registPushCallBack(String id, WsCallBack wsCallBack) {
        pushCallBack.put(id, wsCallBack);
    }

    /**
     * 移除callBack
     */
    public void remove(String id) {
        pushCallBack.remove(id);
    }

    /**
     * 收到消息结果处理
     * */

    public static boolean received(WsResponse response) {
        if(response == null){
            log.warn("response is null");
            return false;
        }
        JSONObject result = response.getResult();
        if(result == null){
            log.warn("response.getResult() is null:{}",response);
            return false;
        }

        String busType = result.getString("bussType");
        if(busType == null || "".equals(busType)){
            log.warn("response.busType is null:{}",response);
            return false;
        }
        WsCallBack callBack = pushCallBack.get(busType);
        if(callBack == null){
            return false;
        }
        //执行注册的CallBack
        callBack.onMsg(response);
        return true;
    }




}
