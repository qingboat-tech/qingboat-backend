package com.qingboat.uc.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserEntity {

    private Integer id;
    private String userName;
    private Date birthday;
    private Boolean sex;
    private String address;
}
