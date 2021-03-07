package com.example.distributelock.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class DemoController {
    private Lock lock = new ReentrantLock();

    @RequestMapping("singleLock")
    public String singleLock() throws Exception {
        log.info("我进入了方法！");
        lock.lock();
        try {
            log.info("我进入了锁！");
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            lock.unlock();
        }
        return "我已经执行完成！";
    }
}
