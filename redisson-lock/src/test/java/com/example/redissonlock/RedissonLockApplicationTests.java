package com.example.redissonlock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedissonLockApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testRedissonLock() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.useSingleServer().setPassword("123456");
        RedissonClient redisson = Redisson.create(config);

        RLock rLock = redisson.getLock("order");

        try {
            rLock.lock(30, TimeUnit.SECONDS);
            log.info("我获得了锁！！！");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info("我释放了锁！！");
            rLock.unlock();
        }
    }

    @Test
    public void poorTest() {


        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.useSingleServer().setPassword("123456");
        RedissonClient redisson = Redisson.create(config);

        // Array  Arrays(辅助工具类)
        // Collection Collections(辅助工具类)
        // Executor Executors(辅助工具类)


        // 一池5个处理线程（用池化技术，一定要记得关闭）
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        // 模拟10个用户来办理业务，每个用户就是一个来自外部请求线程
        try {
            RLock rLock = redisson.getLock("order");

            // 循环十次，模拟业务办理，让5个线程处理这10个请求
            for (int i = 0; i < 10; i++) {
                final int tempInt = i;
                threadPool.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "\t 给用户:" + tempInt + " 办理业务");

                    boolean lock = rLock.tryLock();
                    if (lock) {
                        System.out.println("我获得了锁！！！");
                        System.out.println("我释放了锁！");
                        rLock.unlock();
                    } else {
                        System.err.println("太火爆了没有获取锁！！！");
                    }


                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }



        // 创建10个线程，线程里面进行1000次循环
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                System.out.println("----------");
            }, String.valueOf(i)).start();
        }
    }

}
