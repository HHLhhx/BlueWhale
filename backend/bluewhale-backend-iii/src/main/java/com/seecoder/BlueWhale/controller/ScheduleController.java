package com.seecoder.BlueWhale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.seecoder.BlueWhale.service.OrderService;

//@Configuration
//@EnableScheduling
public class ScheduleController {
//    @Autowired
    OrderService orderService;
    
    @Scheduled(fixedDelay = 10000)
    private void foo() {
        if (!orderService.doClean())
            System.err.println("order clean failed!");
    }
}
