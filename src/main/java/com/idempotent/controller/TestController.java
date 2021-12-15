package com.idempotent.controller;

import com.idempotent.annotation.Idempotent;
import com.idempotent.entity.User;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName： TestController.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述：
 */
@RestController
@RequestMapping("test")
public class TestController {


    @Idempotent(isIdempotent = true,expireTime = 3,timeUnit = TimeUnit.SECONDS,info = "请勿重复提交",delKey = false,haveIp = true)
    @PostMapping("add")
    public ResponseEntity add(@RequestBody User user){
        return ResponseEntity.ok(user);
    }
}
