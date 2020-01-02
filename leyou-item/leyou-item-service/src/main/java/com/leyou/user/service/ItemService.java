package com.leyou.user.service;

import com.leyou.user.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @Author: cuzz
 * @Date: 2018/10/31 18:42
 * @Description:
 */
@Service
public class ItemService {
    public Item svaeItem(Item item) {
        int id = new Random().nextInt(100);
        item.setId(id);
        return item;
    }
}
