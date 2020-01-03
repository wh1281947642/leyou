package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * <code>AuthService</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/03 16:25
 */
@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/03 16:27
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {

        //根据用户名和密码查询用户
        User user = this.userClient.queryUser(username, password);

        //判断用户是否为空
        if(user == null){
            return null;
        }

        //通过jwtUtils生成jwt类型的token
        String token = null;

        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return token = JwtUtils.generateToken(userInfo, this.prop.getPrivateKey(), prop.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
