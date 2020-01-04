package com.leyou.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: cuzz
 * @Date: 2018/11/9 16:03
 * @Description:
 */
@RequestMapping("/category")
public interface CategoryApi {

    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);

}
