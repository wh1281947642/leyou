package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.pojo.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "user:verify:";

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

    /**
     * 发送手机验证码
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/02 17:35
     * @param phone
     * @return
     */
    public void sendVerifyCode(String phone) {

        if (StringUtils.isBlank(phone)){
            return;
        }

        //生成验证码
        String code = NumberUtils.generateCode(6);

        //发送消息到rebbbitMQ
        Map<String, String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","verifycode.sms",msg);

        //把验证码保存到redis中
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone,code,5,TimeUnit.MINUTES);
    }

    /**
     *  用户注册
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/02 19:33
     * @param user
     * @param code
     * @return
     */
    public void register(User user, String code) {

        //查询redis中的验证码
        String redisCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        //校验验证码
        if (!StringUtils.equals(code,redisCode )){
            return;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //加盐加密
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);

        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);

        //删除redis中的验证码
    }
}
