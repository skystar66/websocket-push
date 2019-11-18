package com.socket.server.future;

import com.socket.server.model.WsResponse;

/**
 * @author xuliang
 * @desc: ws-ws 请求，表示异步请求数据，获取实时数据，经常用于订阅等实时场景
 * @date : 2019-10-22
 */
public interface WsCallBack {


    /**
     * 有消息时回调
     */
    public void onMsg(WsResponse response);





}
