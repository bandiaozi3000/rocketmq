package com.rocketmq.demo.rocketmq.controller;

import com.rocketmq.demo.rocketmq.dao.OrderMapper;
import com.rocketmq.demo.rocketmq.model.dto.Order;
import com.rocketmq.demo.rocketmq.model.dto.OrderDTO;
import com.rocketmq.demo.rocketmq.service.OrderService;
import com.rocketmq.demo.rocketmq.util.SnowFlake;
import com.sun.tools.corba.se.idl.constExpr.Or;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.*;

@RestController
public class OrderController {

  private int count = 0;
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    //调整队列数 拒绝服务
    private static ThreadPoolExecutor executor  = new ThreadPoolExecutor(corePoolSize, corePoolSize+1, 10l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100000));

    @Resource
    OrderService orderService;

    @Resource
    OrderMapper orderMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/create_order")
    public void createOrder(@RequestBody OrderDTO order) throws MQClientException {
        logger.info("接收到订单数据：{}",order.getCommodityCode());
        orderService.createOrder(order);
    }

    @PostMapping("/create_order/batch")
    public void createOrderTest(@RequestBody OrderDTO order) throws MQClientException {
     for(int i=0;i<100000;i++){
         Order order1 = new Order();
         order1.setId(SnowFlake.nextId());
         order1.setOrderNo(SnowFlake.nextStr());
         order1.setCommodityCode(order.getCommodityCode());
         order1.setUserId(order.getUserId());
         order1.setAmount(BigDecimal.valueOf(11));
          Runnable runnable = new Runnable() {
              @Override
              public void run() {
                  orderMapper.insert(order1);
              }
          };
         executor.execute(runnable);
     }
    }

    @PostMapping("/create_order/search/thread")
    public Order orderSearchThread(@RequestBody OrderDTO order) throws  InterruptedException, ExecutionException {
        System.out.println(count++);
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("orderNo",order.getOrderNo());
        Callable<Order> callable = new Callable<Order>() {
            @Override
            public Order call() throws Exception {
                Thread.sleep(5000);
                return orderMapper.selectOneByExample(example);
            }
        };
        return executor.submit(callable).get();
    }

    @PostMapping("/create_order/search")
    public Order orderSearch(@RequestBody OrderDTO order) throws InterruptedException {
        System.out.println(count++);
        Thread.sleep(5000);
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("orderNo",order.getOrderNo());
        return orderMapper.selectOneByExample(example);
    }
}