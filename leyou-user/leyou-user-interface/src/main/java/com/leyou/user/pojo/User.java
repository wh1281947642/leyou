package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /***
     *
     */
    private Long id;
    /***
     * 用户名
     */
    private String username;
    /***
     * 密码
     * @JsonIgnore 对象序列化为json字符串时，忽略该属性
     */
    @JsonIgnore
    private String password;
    /***
     * 电话
     */
    private String phone;
    /***
     * 创建时间
     */
    private Date created;
    /***
     * 密码的盐值
     * @JsonIgnore 对象序列化为json字符串时，忽略该属性
     */
    @JsonIgnore
    private String salt;
}