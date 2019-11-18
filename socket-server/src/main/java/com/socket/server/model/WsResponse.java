package com.socket.server.model;

import com.alibaba.fastjson.JSONObject;
import com.socket.server.enums.WsResponseCode;
import lombok.Data;

/**
 * @author :xuliang
 * @desc:响应
 * @date:2019-10-22
 */
@Data
public class WsResponse {

    /**
     * 请求ID
     */
    private long requestId;

    /**
     * 错误信息(ws通信，非业务)
     */
    private String error;

    /**
     * ws通信状态 0-正常 1-超时 2-未知异常
     */
    private WsResponseCode status = WsResponseCode.SUCCESS;

    /**
     * MM响应结果={
     * "SeqNum": 123,
     * "EptFlag": 0,
     * "MsgBody": {
     * "BusType": 20003,
     * "BusData": {
     * "m_ErrCode": 1-失败/0或不存在-成功
     * },
     * "ErrCode": 0
     * }
     * }
     */
    private JSONObject result;

    public WsResponse(WsResponseCode status) {
        this.status = status;
    }

    public WsResponse(long seqNum, JSONObject result) {
        this.status = WsResponseCode.SUCCESS;
        this.requestId = seqNum;
        this.result = result;
    }

}
