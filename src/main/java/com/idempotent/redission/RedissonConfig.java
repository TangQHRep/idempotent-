package com.idempotent.redission;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


/**
 * @ClassName： RedissonConfig.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述： redis 配置类
 */
@Configuration
public class RedissonConfig {

    @Value("${singleServerConfig.address}")
    private String address;

    @Value("${singleServerConfig.password:#{null}}")
    private String password;

    @Value("${singleServerConfig.pingTimeout}")
    private int pingTimeout;

    @Value("${singleServerConfig.connectTimeout}")
    private int connectTimeout;

    @Value("${singleServerConfig.timeout}")
    private int timeout;

    @Value("${singleServerConfig.idleConnectionTimeout}")
    private int idleConnectionTimeout;

    @Value("${singleServerConfig.retryAttempts}")
    private int retryAttempts;

    @Value("${singleServerConfig.retryInterval}")
    private int retryInterval;

    @Value("${singleServerConfig.reconnectionTimeout}")
    private int reconnectionTimeout;

    @Value("${singleServerConfig.failedAttempts}")
    private int failedAttempts;

    @Value("${singleServerConfig.subscriptionsPerConnection}")
    private int subscriptionsPerConnection;

    @Value("${singleServerConfig.subscriptionConnectionMinimumIdleSize}")
    private int subscriptionConnectionMinimumIdleSize;

    @Value("${singleServerConfig.subscriptionConnectionPoolSize}")
    private int subscriptionConnectionPoolSize;

    @Value("${singleServerConfig.connectionMinimumIdleSize}")
    private int connectionMinimumIdleSize;

    @Value("${singleServerConfig.connectionPoolSize}")
    private int connectionPoolSize;


    @Bean(destroyMethod = "shutdown")
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress(address)
                .setPassword(StringUtils.isEmpty(password)?null:password)
                .setIdleConnectionTimeout(idleConnectionTimeout)
                .setConnectTimeout(connectTimeout)
                .setTimeout(timeout)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval)
                .setReconnectionTimeout(reconnectionTimeout)
                .setPingTimeout(pingTimeout)
                .setFailedAttempts(failedAttempts)
                .setSubscriptionsPerConnection(subscriptionsPerConnection)
                .setSubscriptionConnectionMinimumIdleSize(subscriptionConnectionMinimumIdleSize)
                .setSubscriptionConnectionPoolSize(subscriptionConnectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setConnectionPoolSize(connectionPoolSize);
        return (Redisson) Redisson.create(config);
    }

}
