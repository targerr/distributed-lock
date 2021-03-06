package com.example.distributedemo.service;

import com.example.distributedemo.dao.OrderItemMapper;
import com.example.distributedemo.dao.OrderMapper;
import com.example.distributedemo.dao.ProductMapper;
import com.example.distributedemo.model.Order;
import com.example.distributedemo.model.OrderItem;
import com.example.distributedemo.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ProductMapper productMapper;
    //购买商品id
    private int purchaseProductId = 100100;
    //购买商品数量
    private int purchaseProductNum = 1;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private TransactionDefinition transactionDefinition;

    private Lock lock = new ReentrantLock();

    // Spring 手动控制事务需要注释掉
//    @Transactional(rollbackFor = Exception.class)
    public Integer createOrder() throws Exception {

        Product product = null;

        lock.lock();
        try {
            // 开启事务
            TransactionStatus transaction = platformTransactionManager.getTransaction(transactionDefinition);

            product = productMapper.selectByPrimaryKey(purchaseProductId);
            if (product == null) {
                // 事务回滚
                platformTransactionManager.rollback(transaction);
                throw new Exception("购买商品：" + purchaseProductId + "不存在");
            }

            //商品当前库存
            Integer currentCount = product.getCount();
            System.out.println(Thread.currentThread().getName() + "库存数：" + currentCount);
            //校验库存
            if (purchaseProductNum > currentCount) {
                // 事务回滚
                platformTransactionManager.rollback(transaction);
                throw new Exception("商品" + purchaseProductId + "仅剩" + currentCount + "件，无法购买");
            }

            // 计算减库存
            Integer leftCount = currentCount - purchaseProductNum;

//        // 更新库存
//        product.setCount(leftCount);
//        product.setUpdateTime(new Date());
//        product.setUpdateUser("XXXX");
//
//        productMapper.updateByPrimaryKey(product);

            // 使用数据库增量（使用SQL减库存）
            productMapper.updateProductCount(purchaseProductNum, "xxx", new Date(), product.getId());


            // 提交事务
            platformTransactionManager.commit(transaction);
        } finally {
            lock.unlock();
        }


        // 开启事务
        TransactionStatus transaction1 = platformTransactionManager.getTransaction(transactionDefinition);

        Order order = new Order();
        order.setOrderAmount(product.getPrice().multiply(new BigDecimal(purchaseProductNum)));
        //待处理
        order.setOrderStatus(1);
        order.setReceiverName("xxx");
        order.setReceiverMobile("13311112222");
        order.setCreateTime(new Date());
        order.setCreateUser("xxx");
        order.setUpdateTime(new Date());
        order.setUpdateUser("xxx");
        orderMapper.insertSelective(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setPurchasePrice(product.getPrice());
        orderItem.setPurchaseNum(purchaseProductNum);
        orderItem.setCreateUser("xxx");
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        orderItem.setUpdateUser("xxx");
        orderItemMapper.insertSelective(orderItem);

        // 提交事务
        platformTransactionManager.commit(transaction1);

        return order.getId();
    }

}
