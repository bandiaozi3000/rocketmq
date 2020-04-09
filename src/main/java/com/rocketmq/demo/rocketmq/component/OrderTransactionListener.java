package com.rocketmq.demo.rocketmq.component;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.demo.rocketmq.dao.TransactionLogMapper;
import com.rocketmq.demo.rocketmq.model.dto.OrderDTO;
import com.rocketmq.demo.rocketmq.model.dto.TransactionLog;
import com.rocketmq.demo.rocketmq.service.OrderService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class OrderTransactionListener implements TransactionListener {

    @Resource
    OrderService orderService;

    @Resource
    TransactionLogMapper transactionLogMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        logger.info("开始执行本地事务....");
        LocalTransactionState state;
        try{
            String body = new String(message.getBody());
            OrderDTO order = JSONObject.parseObject(body, OrderDTO.class);
            orderService.createOrder(order,message.getTransactionId());
            state = LocalTransactionState.COMMIT_MESSAGE;
            logger.info("本地事务已提交。{}",message.getTransactionId());
        }catch (Exception e){
            logger.info("执行本地事务失败。{}",e);
            state = LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return state;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        logger.info("开始回查本地事务状态。{}",messageExt.getTransactionId());
        LocalTransactionState state;
        String transactionId = messageExt.getTransactionId();
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setId(transactionId);
        if (transactionLogMapper.select(transactionLog).size()>0){
            state = LocalTransactionState.COMMIT_MESSAGE;
        }else {
            state = LocalTransactionState.UNKNOW;
        }
        logger.info("结束本地事务状态查询：{}",state);
        return state;
    }
}