package com.leyou.user.web;

import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * <code>UserController</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/02 10:24
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据是否可用
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/02 10:30
     * @param
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = this.userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }
}
