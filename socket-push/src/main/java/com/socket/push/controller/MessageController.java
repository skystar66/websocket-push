package com.socket.push.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg/")
public class MessageController {


    @RequestMapping("push")
    public String push(@RequestParam("num") int num) {


            return "success";
    }


}
