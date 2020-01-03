package com.leyou.common.pojo;

/**
 * <p>
 * <code>UserInfo</code>
 * </p>
 *  载荷
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/03 15:21
 */
public class UserInfo {

    private Long id;

    private String username;

    public UserInfo() {
    }

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}