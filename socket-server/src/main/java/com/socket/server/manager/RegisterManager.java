package com.socket.server.manager;

import com.socket.server.future.WsCallBack;
import com.socket.server.future.WsFuture;
import com.socket.server.model.WsResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuliang
 * @desc : 各类消息注册
 * @date : 2019-10-22
 */
@Slf4j
public class RegisterManager {


    /**
     * Future管理 1.正常:注册删除，不停留 2.超时:由超时删除 3.回调无future,超时丢弃消息(打警告日志)
     */
    private static final Map<Long, WsFuture> mapFuture = new ConcurrentHashMap<>();

    /**
     * 设置订阅相关回调 处理
     */
    private static final Map<Long, WsCallBack> mapCallBack = new ConcurrentHashMap<>();


    /**
     * 注册callback
     */
    public static void registCallBack(Long requestId, WsCallBack wsCallBack) {
        mapCallBack.put(requestId, wsCallBack);
    }

    /**
     * 注册future
     */
    public static void registFuture(WsFuture wsFuture) {
        mapFuture.put(wsFuture.getId(), wsFuture);
    }

    /**
     * 收到消息处理
     */
    public static void recived(WsResponse wsResponse) {
        //校验是否是push消息
        boolean isPushMsg = PushManager.received(wsResponse);
        if (isPushMsg) {
            return;
        }
        //获取同步消息
        long requestId = wsResponse.getRequestId();
        //request 唯一
        if (mapFuture.containsKey(requestId)) {
            //处理future 模式
            WsFuture wsFuture = mapFuture.remove(requestId);
            wsFuture.reciveResponse(wsResponse);
        } else if (mapCallBack.containsKey(requestId)) {
            //处理异步模式
            WsCallBack wsCallBack = mapCallBack.remove(requestId);
            wsCallBack.onMsg(wsResponse);
        }
    }

}
