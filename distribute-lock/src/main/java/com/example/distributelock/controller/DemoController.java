package com.example.distributelock.controller;

import com.example.distributelock.dao.DistributeLockMapper;
import com.example.distributelock.model.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class DemoController {


    @Resource
    private DistributeLockMapper distributeLockMapper;

    @RequestMapping("singleLock")
    @Transactional(rollbackFor = Exception.class)
    public String singleLock() throws Exception {
        log.info("我进入了方法！");

        DistributeLock distributeLock = distributeLockMapper.selectDistributeLock("demo");
        if (distributeLock == null){
            throw new RuntimeException("分布式锁找不到！！");
        }
        try {
            log.info("我进入了锁！");
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("我已经执行完成！");
        return "我已经执行完成！";
    }
}
