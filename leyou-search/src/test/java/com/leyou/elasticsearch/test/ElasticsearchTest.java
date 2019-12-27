package com.leyou.elasticsearch.test;

import com.leyou.common.vo.PageResult;
import com.leyou.item.vo.SpuVo;
import leyou.com.LeyouSearchApplication;
import leyou.com.search.client.GoodsClient;
import leyou.com.search.pojo.Goods;
import leyou.com.search.repository.GoodsRepository;
import leyou.com.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <code>ElasticsearchTest</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/27 10:30
 */
@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test(){
        //创建索引库（数据库）
        this.elasticsearchTemplate.createIndex(Goods.class);
        //创建映射（表）
        this.elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;
        do {
            //分页查询spu
            PageResult<SpuVo> result = this.goodsClient.querySpuByPage(null, null, page, rows);
            //获取当前页的数据
            List<SpuVo> items = result.getItems();
            //由List<spuVo> ->List<goods>
            List<Goods> goodsList = items.stream().map(spuVo -> {
                try {
                    return this.searchService.buildGoods(spuVo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            //执行新增数据的方法
            this.goodsRepository.saveAll(goodsList);
            rows = items.size();
            page++;
        }while (rows==100);
    }
}
