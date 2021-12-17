package com.idempotent.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName： RedisSimpleLock.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月17日 14:59:00
 * @功能描述： 一个非常简单的分布式锁工具
 */
public class RedisSimpleLock {

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 释放锁lua脚本
     */
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    /**
     * 释放锁成功返回值
     */
    private static final Long RELEASE_LOCK_SUCCESS_RESULT = 1L;

    /**
     * @param keyName            锁名称
     * @param uuid               锁值
     * @param holdingTime        持有时间
     * @param timeUnit           单位
     * @param numberOfRetries    重试次数
     * @param waitingMillisecond 等待时间
     * @return true：加锁成功 false：加锁失败
     */
    public Boolean padLockRetry(String keyName, String uuid, Long holdingTime, TimeUnit timeUnit, int numberOfRetries, long waitingMillisecond) {
        Boolean flag;
        for (int i = 0; i < numberOfRetries; i++) {
            flag = redisTemplate.opsForValue().setIfAbsent(keyName, uuid, holdingTime, timeUnit);
            if (flag) {
                return true;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(waitingMillisecond);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    /**
     * 释放锁
     *
     * @param key
     * @param uuid
     * @return
     */
    public Boolean releaseLock(String key, String uuid) {
        return redisTemplate.execute(
                (RedisConnection connection) -> connection.eval(
                        RELEASE_LOCK_LUA_SCRIPT.getBytes(),
                        ReturnType.INTEGER,
                        1,
                        key.getBytes(),
                        uuid.getBytes())
        ).equals(RELEASE_LOCK_SUCCESS_RESULT);
    }
}
