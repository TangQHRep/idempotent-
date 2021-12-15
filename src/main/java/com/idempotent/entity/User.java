package com.idempotent.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName： User.java
 * @author： Tangqh
 * @version： 1.0.0
 * @createTime： 2021年12月10日
 * @功能描述：user实体类
 */
@Data
public class User {

    private String name;

    private Integer age;

    private String province;

    private String city;

}
