package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.user.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * <code>GoodsService</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/31 10:05
 */
@Service
public class GoodsService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    /**
     * 封装商品详情页数据
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/31 09:27
     * @param spuId
     * @return 
     */
    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        //根据spuId查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);

        //查询分类 map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        //初始化一个分类的map
        List<Map<String,Object>>  categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String,Object> categorie = new HashMap<>();
            categorie.put("id",cids.get(i));
            categorie.put("name",names.get(i));
            categories.add(categorie);
        }

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //skus
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);
        //查询规格参数组
        List<SpecGroup> specGroups = this.specificationClient.queryGroupsWithParam(spu.getCid3());
        //查询特殊的规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParams(null, spu.getCid3(), null, false);
        //初始化特殊的规格参数
        Map<Long,Object> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            paramMap.put(specParam.getId(),specParam.getName());
        });

        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",specGroups);
        model.put("paramMap",paramMap);
        return model;
    }
}
