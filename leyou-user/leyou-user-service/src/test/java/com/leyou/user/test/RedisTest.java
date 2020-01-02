package com.leyou.user.test;

import com.leyou.LeyouUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * <code>RedisTest</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/02 16:51
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouUserApplication.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis(){
        //存储数据
        this.stringRedisTemplate.opsForValue().set("key1","value1");
        //获取数据
        String val = this.stringRedisTemplate.opsForValue().get("key2");
        System.out.println("val=" + val);
    }

    @Test
    public void testRedis2(){
        //存储数据,并指定剩余时间
        this.stringRedisTemplate.opsForValue().set("key3","value3",50,TimeUnit.SECONDS);
        //获取数据
        String val = this.stringRedisTemplate.opsForValue().get("key3");
        System.out.println("val=" + val);
    }

    @Test
    public void testHash(){
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps("user1");
        //操作hash数据
        hashOps.put("name","jack");
        hashOps.put("age","21");

        //获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name=" + name);

        //获取所有数据
        Map<Object, Object> map = hashOps.entries();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();
            System.out.println(key+":"+val);
        }
    }


}
