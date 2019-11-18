package com.socket.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.socket.client.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private WebSocketClient webSocketClient;


    @Override
    public void groupSending(String stockIds) {

        JSONObject json = new JSONObject();
        json.put("stockIds", stockIds);
        json.put("event", 0);
        String ids = json.getString("stockIds");
        Integer event = json.getInteger("event");


//        webSocketClient.send(message+"---6666");

    }

    @Override
    public void appointSending(String name, String message) {

    }


    @Override
    public void sub(String stockIds,Integer type) {
        JSONObject json = new JSONObject();
        json.put("stockIds", stockIds);
        json.put("event", type);
        webSocketClient.send(json.toJSONString());
    }

    @Override
    public void unSub(String stockIds) {
        JSONObject json = new JSONObject();
        json.put("stockIds", stockIds);
        json.put("event", 1);
        webSocketClient.send(json.toJSONString());
    }
}
