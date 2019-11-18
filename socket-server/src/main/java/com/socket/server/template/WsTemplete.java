package com.socket.server.template;

import com.socket.server.future.WsCallBack;
import com.socket.server.future.WsFuture;
import com.socket.server.model.WsRequest;

/**
 * @author :liangxul
 * @desc: 共享Templete, 适用于不需要token的地方
 * @date: 2019-10-22
 */
public interface WsTemplete {


    /**
     * 发送请求,获得异步结果句柄
     * <pre>
     *     WsFuture future=sendByFuture(req);
     *     WsResponse response=future.get();//获得结果
     * </pre>
     *
     * @param request
     * @return
     */
    WsFuture sendAsynByFuture(WsRequest request);


    /**
     * 发送请求,响应触发回调
     * <pre>
     *    sendByCallBack(req,new WsCallBack(){
     *         public onMsg(WsResponse response){
     *            //结果处理
     *         }
     *    });
     * </pre>
     *
     * @param request
     * @param callBack
     * @return
     */

    void sendByCallBack(WsRequest request, WsCallBack callBack);


    /**
     * 行情订阅
     *
     * @param stockId
     */
    public void subscribe(String stockId);

    /**
     * 取消行情订阅
     *
     * @param stockId
     */
    public void canalSubscribe(String stockId);


}
