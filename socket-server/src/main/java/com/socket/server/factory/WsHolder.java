package com.socket.server.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.socket.server.future.WsCallBack;
import com.socket.server.model.WsContext;
import com.socket.server.model.WsResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuliang
 * @desc:连接持有者
 * @date:2019-10-22
 */
@Data
@Slf4j
public class WsHolder {

    /**
     * 订阅列表
     */
    private static final Map<String, String> subMap = new ConcurrentHashMap<>();

    /**
     * 连接上下文
     */
    private WsContext wsContext;

    /**
     * ws-push
     */

    private WebSocketClient webSocketClient = null;

    /**
     * 最后(重连)连接时间
     */
    private Date lastConnTime = new Date();

    /**
     * 最后活跃时间,可以有守护线程,来删除空闲的连接
     */
    private Date lastActiveTime = new Date();


    private WsCallBack wsCallBack;

    public WsHolder(WsContext wsContext, WsCallBack callBack) {
        this.wsContext = wsContext;
        this.wsCallBack = callBack;
    }

    /**
     * 创建连接
     */
    public synchronized void createConnect() {
        String url = getWsUrl();
        log.info(">>>>> 开始创建ws连接, url:{}", url);
        try {
            //连接ws
            webSocketClient = new WebSocketClient(new URI(url)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info(">>>>>> ws连接建立成功！url：{}", url);
                }

                @Override
                public void onMessage(String s) {
                    log.info(">>>>> ws收到消息, msg:{}", s);
                    reciveMsg(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    //这里只打印日志，让守护线程去重连
                    log.info("链接已关闭,i={},s={},b={},{}", i, s, b, wsContext);

                }

                @Override
                public void onError(Exception e) {
                    // 这里只打日志,让守护线程去重连
                    log.error("ws连接发生错误！" + e.getMessage(), e);
                }
            };

            //开始连接
            webSocketClient.connect();
            //等待连接s
            int f = 0;
            while (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) {
                log.info(">>>>>> websocket  等待连接中.........");
                Thread.sleep(500);
                f++;
                if (f > 10) {
                    log.warn("连接超时,5s内没有重连成功,等待守护线程，下次重试");
                    break;
                }
            }
            lastConnTime = new Date();
        } catch (Exception ex) {
            log.error(">>>>> 创建连接失败, url:{}", url);
            ex.printStackTrace();
        }
    }


    /**
     * 重连
     */

    public void reConnect() {
        if (webSocketClient == null) {
            log.info(">>>>>> 当前wsClient  未连接！");
            return;
        }
        if (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) {
            synchronized (this) {
                if (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) {

                    try {
                        webSocketClient.reconnect();
                        //等待连接s
                        int f = 0;
                        while (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) {
                            log.info(">>>>>> websocket  等待连接中.........");
                            Thread.sleep(500);
                            f++;
                            if (f > 10) {
                                log.warn("连接超时,5s内没有重连成功,等待守护线程，下次重试");
                                break;
                            }
                        }
//                        lastConnTime = new Date();
                    } catch (Exception ex) {
                        log.error(">>>>>> 发起重连 出现问题，error：{}", ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 关闭连接
     */
    public void close() {

        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }


    /**
     * 获取ws url
     */
    private String getWsUrl() {

        if (wsContext == null) {
            throw new RuntimeException("The [wsContext] is null");
        }

        if (wsContext.getUrl() == null) {
            throw new RuntimeException("The [wsContext.url] is null");
        }
        return wsContext.getUrl();
    }


    /**
     * 收到消息
     */
    private void reciveMsg(String msg) {
        JSONObject json = JSON.parseObject(msg);
        Long seqNum = json.getLong("SeqNum");
        WsResponse wsResponse = new WsResponse(seqNum, json);
        wsCallBack.onMsg(wsResponse);
    }


    /**
     * 判断是否包含该订阅内容
     */
    public boolean isSubId(String stockId) {
        return subMap.containsKey(stockId);
    }

    /**
     * 添加订阅
     */
    public void addSubId(String stockId) {
        subMap.put(stockId, "");
    }

    /**
     * 删除订阅
     */
    public void delSubId(String stockId) {
        subMap.remove(stockId);
    }

    /**
     * 获取订阅集合
     */
    public Map<String, String> getSubmap() {
        return subMap;
    }


    /**
     * 校验连接是否打开
     */

    public boolean isRuning() {
        if (webSocketClient == null) {
            return false;
        }
        return webSocketClient.getReadyState().equals(ReadyState.OPEN);
    }
}
