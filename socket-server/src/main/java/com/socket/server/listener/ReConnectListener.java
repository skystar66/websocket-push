package com.socket.server.listener;

import com.socket.server.factory.WsHolder;

/**
 * @author xuliang
 * @desc:重连监听器
 * @date:2019-10-22
 */
public interface ReConnectListener {

    /**
     * 重连会通知此监听器,在这里可实现重连后需要处理的逻辑
     * 如重新订阅等
     *
     * @param wsHolder
     */
    void onReConnect(WsHolder wsHolder);


}
