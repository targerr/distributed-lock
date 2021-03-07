package com.example.distributelock.controller;

import com.example.distributelock.lock.RedisLock;
import com.example.distributelock.lock.ZkLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class RedisLockController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("redisLock")
    public String redisLock() {

        log.info("我进入了方法！");
        try (RedisLock redisLock = new RedisLock(redisTemplate,"redisKey",30)){
            if (redisLock.getLock()) {
                log.info("我进入了锁！！");
                Thread.sleep(15000);
            }else {
                log.info("太火爆了，挤不进去！！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成");
        return "方法执行完成";
    }


//    public boolean getLock(String key, String value, int expireTime) {
//        RedisCallback<Boolean> redisCallback = connection -> {
//            //设置NX
//            RedisStringCommands.SetOption setOption = RedisStringCommands.SetOption.ifAbsent();
//            //设置过期时间
//            Expiration expiration = Expiration.seconds(expireTime);
//            //序列化key
//            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
//            //序列化value
//            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
//            //执行setnx操作
//            Boolean result = connection.set(redisKey, redisValue, expiration, setOption);
//            return result;
//        };
//
//        //获取分布式锁
//        Boolean lock = (Boolean) redisTemplate.execute(redisCallback);
//        return lock;
//    }
//
//    public boolean unLock(String key, String value) {
//        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
//                "    return redis.call(\"del\",KEYS[1])\n" +
//                "else\n" +
//                "    return 0\n" +
//                "end";
//        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
//        List<String> keys = Arrays.asList(key);
//
//        Boolean result = (Boolean) redisTemplate.execute(redisScript, keys, value);
//        log.info("释放锁的结果：" + result);
//        return result;
//    }

}
