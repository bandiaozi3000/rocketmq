package com.rocketmq.demo.rocketmq.service;

import com.rocketmq.demo.rocketmq.model.dto.OrderDTO;
import org.apache.rocketmq.client.exception.MQClientException;

public interface OrderService {

     void createOrder(OrderDTO orderDTO, String transactionId);

     void createOrder(OrderDTO order) throws MQClientException;
}
