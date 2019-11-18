package com.socket.client.controller;


import com.socket.client.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: xuliang
 * @Date: 2019/10/11 16:47
 * @Description: 测试后台websocket客户端
 */
@RestController
@RequestMapping("/websocket")
public class WebSocketController {


    @Autowired
    WebSocketService webSocketService;


    @GetMapping("/sendMessage")
    public String sendMessage(@RequestParam("event") Integer event,
                              @RequestParam("num") Integer num) {
        String stockIds = "";
        for (int i = 0; i < num; i++) {
            stockIds = stockIds + "," + i;
        }
        webSocketService.sub(stockIds, event);
        return stockIds;
    }

}
