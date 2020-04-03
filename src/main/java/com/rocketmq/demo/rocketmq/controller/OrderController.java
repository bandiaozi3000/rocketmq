package com.rocketmq.demo.rocketmq.controller;

import com.rocketmq.demo.rocketmq.model.dto.OrderDTO;
import com.rocketmq.demo.rocketmq.service.OrderService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class OrderController {

    @Resource
    OrderService orderService;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/create_order")
    public void createOrder(@RequestBody OrderDTO order) throws MQClientException {
        logger.info("接收到订单数据：{}",order.getCommodityCode());
        orderService.createOrder(order);
    }
}