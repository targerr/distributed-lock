package com.example.distributedemo;

import com.example.distributedemo.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistributeDemoApplicationTests {
    @Autowired
    private OrderService orderService;

    @Test
    public void concurrentOrder() throws InterruptedException {
//        Thread.sleep(60000);
        CountDownLatch cdl = new CountDownLatch(5);
        // 让线程等待某一时刻，同时请求（等待5个线程）
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        ExecutorService es = Executors.newFixedThreadPool(5);
        for (int i =0;i<5;i++){
            es.execute(()->{
                try {
                    cyclicBarrier.await();
                    Integer orderId = orderService.createOrder();
                    System.out.println("订单id："+orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    cdl.countDown();
                }
            });
        }
        // 等待线程防止主线程完毕后，关闭数据库连接
        cdl.await();
        es.shutdown();
    }

}
