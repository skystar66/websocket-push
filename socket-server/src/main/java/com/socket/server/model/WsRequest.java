package com.socket.server.model;

import com.socket.server.constants.WsConstants;
import com.socket.server.util.RequestUtil;
import lombok.Data;

/**
 * @desc:请求实体封装
 * @auth:xuliang
 * @date:2019-10-22
 * */
@Data
public class WsRequest {


    /*################外层:EptFlag,SeqNum,MsgBody######################*/
    /**
     * 请求ID,默认AtomicLong自增,即美美接口:SeqNum
     */
    private long requestId=RequestUtil.newId();

    /**
     * 加密标志位(1为加密),默认0不加密
     */
    private int eptFlag=0;

    /*################内层:msgBody={BusType,BusData}######################*/
    /**
     * 请求数据
     * msgBody={
     * 		BusType=接口标识
     * 		BusData=正文
     * }
     */
    private String msgBody;

    /**
     * 请求超时时间,不设置取系统预设
     */
    private int timeout=WsConstants.DEFAULT_TIMEOUT;



    public WsRequest(String msgBody) {
        super();
        this.msgBody = msgBody;
    }





}
