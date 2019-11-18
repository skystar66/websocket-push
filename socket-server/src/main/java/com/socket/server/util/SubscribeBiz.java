package com.socket.server.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订阅工具
 *
 * @author xuliang
 * @date 2019年7月23日
 */
public class SubscribeBiz {

    private final static Logger logger = LoggerFactory.getLogger(SubscribeBiz.class);

    // 取消订阅
    public static String canalSubMsgBody(String stockId) {

//		if (!stockId.startsWith("US")) {
//			stockId = "US" + stockId;
//		}
//
//		// MsgBody
//		JSONObject msgBody = new JSONObject();
//		msgBody.put("BusType", 20002);
//
//		// busData
//		JSONObject busData = new JSONObject();
//		busData.put("m_StockID", stockId);
//
//		msgBody.put("BusData", busData);

        return stockId;
    }

    // 订阅
    public static String subMsgBody(String stockId) {

//		if (!stockId.startsWith("US")) {
//			stockId = "US" + stockId;
//		}
//
//		// MsgBody
//		JSONObject msgBody = new JSONObject();
//		msgBody.put("BusType", 20003);
//
//		// busData
//		JSONObject busData = new JSONObject();
//		busData.put("m_StockID", asArrays(stockId));
//		busData.put("m_iSubcribeFlag", 0);
//
//		msgBody.put("BusData", busData);

        return stockId;
    }

    private static JSONArray asArrays(String... ps) {

        JSONArray tmp = new JSONArray();
        for (String p : ps) {
            tmp.add(p);
        }

        return tmp;
    }

    public static boolean isSuccess(JSONObject result) {

        if (result == null) {
            return false;
        }

        JSONObject msgBody = result.getJSONObject("MsgBody");
        if (msgBody == null) {
            return false;
        }

        String errCode = msgBody.getString("ErrCode");
        //成功:不存在或为0
        if (errCode == null || "0".equals(errCode)) {

            JSONObject busData = msgBody.getJSONObject("BusData");
            if (busData == null) {
                return false;
            }

            String m_ErrCode = busData.getString("m_ErrCode");
            //成功:不存在或为0
            if (m_ErrCode == null || "0".equals(m_ErrCode)) {
                return true;
            }
        }

        logger.warn("MM响应数据,状态异常:{}", result);

        return false;
    }
}
