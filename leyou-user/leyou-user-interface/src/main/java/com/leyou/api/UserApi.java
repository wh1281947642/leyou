package com.leyou.api;

import com.leyou.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * <code>UserApi</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 14:54
 */
public interface UserApi {

    /**
     * 根据用户名和密码查询用户
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/02 20:39
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);
}
