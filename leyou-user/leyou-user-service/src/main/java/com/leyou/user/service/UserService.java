package com.leyou.user.service;

import com.leyou.pojo.User;
import com.leyou.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * <code>UserService</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/02 10:22
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    /**
     * 校验数据是否可用
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/02 10:31
     * @param
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }
}
