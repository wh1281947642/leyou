package com.leyou.item.service;

import com.leyou.user.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * <p>
 * <code>ItemService</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/02 18:53
 */
@Service
public class ItemService {
    public Item svaeItem(Item item) {
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}
