package com.idempotent.aspect;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.idempotent.exception.IdempotentException;
import com.idempotent.annotation.Idempotent;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @ClassName： IdempotentAspect.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述： 幂等切面
 */
@Aspect
@Component
public class IdempotentAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentAspect.class);

    //存当前主机线程内的需进行幂等校验的程序，并根据配置判断是否程序完成后删除key值
    private ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal();

    private static final String RMAPCACHE_KEY = "idempotent";

    private static final String KEY = "key";

    private static final String DELKEY = "delKey";

    @Autowired
    private Redisson redisson;



    @Pointcut("@annotation(com.idempotent.annotation.Idempotent)")
    public void pointCut(){}

    @Before("pointCut()")
    public void beforePointCut(JoinPoint joinPoint)throws Exception{
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        if(!method.isAnnotationPresent(Idempotent.class)){
            return;
        }

        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        boolean isIdempotent = idempotent.isIdempotent();
        if(!isIdempotent){
            return;
        }

        StringBuffer keyBuffer = new StringBuffer();

        //是否需要针对ip进行校验
        if(idempotent.haveIp()){
            String ip=ServletUtil.getClientIP(request,null);
            keyBuffer.append(ip);
        }

        //处理请求信息
        String argString  = Arrays.asList(joinPoint.getArgs()).toString();

        keyBuffer.append(argString);

        //信息脱敏
        String key = SecureUtil.md5(keyBuffer.toString());

        long expireTime = idempotent.expireTime();
        String info = idempotent.info();
        TimeUnit timeUnit = idempotent.timeUnit();
        boolean delKey = idempotent.delKey();

        RMapCache<String, Object> rMapCache = redisson.getMapCache(RMAPCACHE_KEY);
        String value = LocalDateTime.now().toString().replace("T", " ");
        Object v1;
        if (null != rMapCache.get(key)){
            throw new IdempotentException(info);
        }
        //避免线程安全问题
        synchronized (this){
            try{
                v1 = rMapCache.putIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
                if(null != v1){
                    throw new IdempotentException(info);
                }else {
                    LOGGER.info("[idempotent]:key={},value={},expireTime={}{},now={}",key,value,expireTime,timeUnit,LocalDateTime.now().toString());
                }
            } catch (IdempotentException e) {
                throw e;
            } catch (Exception e){
                rMapCache.fastRemove(key);
                LOGGER.error("幂等性校验出错：" + e.getMessage(), e);
                throw e;
            }finally {
                Map<String, Object> map =
                        CollectionUtils.isEmpty(threadLocal.get()) ? new HashMap<>(4):threadLocal.get();
                map.put(KEY,key);
                map.put(DELKEY,delKey);
                threadLocal.set(map);
            }
        }
    }

    @After("pointCut()")
    public void afterPointCut(JoinPoint joinPoint){
        //FIXME 切面前置方法如果已经避免的线程安全问题，推测切面后置后发应该不会出现线程安全问题，待验证
        Map<String,Object> map = threadLocal.get();
        if(CollectionUtils.isEmpty(map)){
            return;
        }

        RMapCache<Object, Object> mapCache = redisson.getMapCache(RMAPCACHE_KEY);
        if(mapCache.size() == 0){
            return;
        }

        String key = map.get(KEY).toString();
        boolean delKey = (boolean)map.get(DELKEY);

        if(delKey){
            mapCache.fastRemove(key);
            LOGGER.info("[idempotent]:has removed key={}",key);
        }
        threadLocal.remove();
    }

    //TODO 基于token的幂等验证待编写
}
