package com.rocketmq.demo.rocketmq.service.impl;

import com.alibaba.fastjson.JSON;
import com.rocketmq.demo.rocketmq.component.TransactionProducer;
import com.rocketmq.demo.rocketmq.dao.OrderMapper;
import com.rocketmq.demo.rocketmq.dao.TransactionLogMapper;
import com.rocketmq.demo.rocketmq.model.dto.Order;
import com.rocketmq.demo.rocketmq.model.dto.OrderDTO;
import com.rocketmq.demo.rocketmq.model.dto.TransactionLog;
import com.rocketmq.demo.rocketmq.service.OrderService;
import com.rocketmq.demo.rocketmq.util.SnowFlake;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    OrderMapper orderMapper;
    @Resource
    TransactionLogMapper transactionLogMapper;
    @Autowired
    TransactionProducer producer;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //执行本地事务时调用，将订单数据和事务日志写入本地数据库
    @Transactional
    @Override
    public void createOrder(OrderDTO orderDTO, String transactionId){

        //1.创建订单
        Order order = new Order();

        BeanUtils.copyProperties(orderDTO,order);
        orderMapper.insert(order);

        //2.写入事务日志
        TransactionLog log = new TransactionLog();
        log.setId(transactionId);
        log.setBusiness("order");
        log.setForeignKey(String.valueOf(order.getId()));
        transactionLogMapper.insert(log);

        logger.info("订单创建完成。{}",orderDTO);
    }

    //前端调用，只用于向RocketMQ发送事务消息
    @Override
    public void createOrder(OrderDTO order) throws MQClientException {
        order.setId(SnowFlake.nextId());
        order.setOrderNo(SnowFlake.nextStr());
        producer.send(JSON.toJSONString(order),"order");
    }
}