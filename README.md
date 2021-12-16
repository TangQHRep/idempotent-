# idempotent 幂等处理方案

## 一、方案一

### 1.原理

1.请求开始前，根据key查询
查到结果：报错
未查到结果：存入key-value-expireTime
key=ip+args

2.请求结束后，直接删除key
不管key是否存在，直接删除
是否删除，可配置

3.expireTime过期时间，防止一个请求卡死，会一直阻塞，超过过期时间，自动删除
过期时间要大于业务执行时间，需要大概评估下;

4.此方案直接切的是接口请求层面。

5.过期时间需要大于业务执行时间，否则业务请求1进来还在执行中，前端未做处理，或者用户跳转页面后再回来做重复请求2，结果依旧是不符合预期的。

6.建议delKey = false。即使业务执行完，也不删除key，强制锁expireTime的时间。预防5的情况发生。

7.实现思路：同一个请求ip(可选)和接口，相同参数的请求，在expireTime内多次请求，只允许成功一次。

8.页面做遮罩，数据库层面的唯一索引，先查询再添加，等处理方式应该都处理下。

9.此注解只用于幂等，不用于锁，100个并发这种压测，会出现问题，在这种场景下也没有意义，实际中用户也不会出现1s或者3s内手动发送了50个或者100个重复请求,或者弱网下有100个重复请求；


### 2.使用

引入注解，切面，配置类，异常类，修改配置，直接在需要使用的接口上添加注解即可；
（后期会优化为jar）
使用如下：

```java
    @Idempotent(idempotent = true,expireTime = 3,timeUnit = TimeUnit.SECONDS,info = "请勿重复添加用户",delKey = false,haveIp=true)
    @GetMapping(value = "add")
    public String add(User user){
        return "添加成功";
    }
```

## 二、方案二

1.原理

使用uuid创建唯一token，请求成功与否都进行删除，没有方案一的各项配置项，实现简单，不再另外说明

2.使用

```java
//创建token
@GetMapping
public ServerResponse token() {
    return tokenService.createToken();
}
//加入注解 查看请求头中是否含有token
@TokenIdempotent
@RequestMapping("testIdempotent")
public ServerResponse testIdempotent() {
    return ServerResponse.success("test idempotent success");
}
```
