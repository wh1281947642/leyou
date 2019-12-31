package com.leyou.goods.web;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * <p>
 * <code>GoodsController</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/30 16:55
 */
@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param spuId
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(@PathVariable("id")Long spuId,Model model){

        Map<String, Object> map = this.goodsService.loadData(spuId);
        model.addAllAttributes(map);
        this.goodsHtmlService.createHtml(spuId);
        return "item";
    }
}