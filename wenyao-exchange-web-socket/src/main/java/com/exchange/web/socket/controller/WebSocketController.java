package com.exchange.web.socket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/socket")
public class WebSocketController {


    @RequestMapping("/aaaa")
    public String dashboard() {

        log.info("-------------aaaa:");

        return "";
    }

}
