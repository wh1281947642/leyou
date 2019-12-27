package leyou.com.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.item.vo.SkuVo;
import leyou.com.search.client.BrandClient;
import leyou.com.search.client.CategoryClient;
import leyou.com.search.client.GoodsClient;
import leyou.com.search.client.SpecificationClient;
import leyou.com.search.pojo.Goods;
import leyou.com.search.pojo.SearchRequest;
import leyou.com.search.pojo.SearchResult;
import leyou.com.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * <code>SearchService</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 15:13
 */
@Slf4j
@Service
public class SearchService {
    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRepository repository;

    @Autowired
    private ElasticsearchTemplate template;


    private  static  final ObjectMapper MAPPER = new  ObjectMapper();

    /**
     *  根据spu构建goods对象
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/26 15:13
     * @param
     * @return 
     */
    public Goods buildGoods(Spu spu) throws IOException {

        //根据分类id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //根据spuId查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化一个价格集合，手机所有sku的价格
        List<Long> prices = new ArrayList<>();
        //收集sku必要的字段信息
        List<Map<String,Object>> skuMapList = new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，数据库的图片，多张是以"  , "分隔，所以业以逗号切割，返回图片数组，获取第一张图片
            map.put("image",StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),"")[0]);
            skuMapList.add(map);
        });

        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.querySpecParams(null, spu.getCid3(), null, true);

        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //把特殊的规格参数值进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        Map<String ,Object> specs = new HashMap<>();
        params.forEach(specParam -> {
            //判断规格参数的类型是否是通用的规格参数
            if (specParam.getGeneric()){
                //如果是通用类型的参数，从genericSpecMap获取规格参数值
                String value = genericSpecMap.get(specParam.getId().toString()).toString();
                //判断是否是数值类型，如果是数值类型，应该返回一个区间
                if (specParam.getNumeric()){
                    value = chooseSegment(value, specParam);
                }
                specs.put(specParam.getName(),value);
            }else {
                //如果是特殊类型的参数，从specialSpecMap获取规格参数值
                List<Object> value = specialSpecMap.get(specParam.getId().toString());
                specs.put(specParam.getName(),value);
            }
        });

        Goods goods = new Goods();
        //主键
        goods.setId(spu.getId());
        //一级分类id
        goods.setCid1(spu.getCid1());
        //二级分类id
        goods.setCid2(spu.getCid2());
        //三级分类id
        goods.setCid2(spu.getCid3());
        //品牌id
        goods.setBrandId(spu.getBrandId());
        //创建时间
        goods.setCreateTime(spu.getCreateTime());
        //副标题
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称和品牌名称
        goods.setAll(spu.getTitle()+" "+ StringUtils.join(names," ") + " " + brand.getName());
        //获取spu下的所有sku价格
        goods.setPrice(prices);
        //获取spu下的所有sku，并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有的查询规格参数{name:value}
        goods.setSpecs(specs);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /*private HashMap<String, Object> getSpecs(Spu spu) {
        // 获取规格参数
        List<SpecParam> params = specificationClient.querySpecParams(null, spu.getCid3(), true, null);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        // 查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        // 获取通用规格参数
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //定义spec对应的map
        HashMap<String, Object> map = new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for (SpecParam param : params) {
            //key是规格参数的名称
            String key = param.getName();
            Object value = "";

            if (param.getGeneric()) {
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(param.getId());
                if (param.getNumeric()) {
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                //参数不是通用类型
                value = specialSpec.get(param.getId());
            }
            value = (value == null ? "其他" : value);
            //存入map
            map.put(key, value);
        }
        return map;
    }

    private List<SkuVo> getSkuVo(List<Sku> skus) {
        List<SkuVo> skuVoList = new ArrayList<>();
        for (Sku sku : skus) {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(sku.getId());
            skuVo.setPrice(sku.getPrice());
            skuVo.setTitle(sku.getTitle());
            skuVo.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            skuVoList.add(skuVo);
        }
        return skuVoList;

    }

    private Set<Long> getPrices(List<Sku> skuList) {
        // 查询sku
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        return skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());
    }

    private String getall(Spu spu) {
        // 查询分类
        List<Category> categories = categoryClient.queryCategoryListByids(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        // 搜索字段
        String all = spu.getTitle() + StringUtils.join(names, ",") + brand.getName();
        return all;
    }



    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage() - 1; // elasicsearch默认page是从0开始
        int size = request.getSize();
        // 查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));
        // 分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 过滤
        QueryBuilder query = buildBaseQuery(request);
        queryBuilder.withQuery(query);
        // 聚合分类品牌
        String categoryAggName = "category_agg";
        String brandAggName = "brand_agg";
        // 对商品分类进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 对品牌进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));


        // 查询
        // Page<Goods> result = repository.search(queryBuilder.build());
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        ;
        long total = result.getTotalElements();
        long totalPage = result.getTotalPages();
        List<Goods> content = result.getContent();

        // 解析聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category> categories = getCategoryAggResult(aggs.get(categoryAggName));
        List<Brand> brands = getBrandAggResult(aggs.get(brandAggName));

        // 完成规格参数聚合
        // 判断商品分类数量，看是否需要对规格参数进行聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            // 如果分类只剩下一个，才进行规格参数过滤
            specs = getSpecs(categories.get(0).getId(), query);
        }


        // 返回结果
        return new SearchResult(total, totalPage, result.getContent(), categories, brands, specs);
    }

    private QueryBuilder buildBaseQuery(SearchRequest request) {
        // 创建布尔查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        // 过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            // 处理key
            if (!"cid3".equals(key) && ! "brandId".equals(key)) {
                key = "spec." + key + ".keyword";
            }
            queryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return queryBuilder;
    }

    // 解析品牌聚合结果
    private List<Brand> getBrandAggResult(LongTerms terms) {
        try {

            List<Long> ids = terms.getBuckets().stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            log.error("[搜索服务]查询品牌异常", e);
            return null;
        }
    }

    // 解析商品分类聚合结果
    private List<Category> getCategoryAggResult(LongTerms terms) {
        try {

            List<Long> ids = terms.getBuckets().stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryListByids(ids);
            return categories;
        } catch (Exception e) {
            log.error("[搜索服务]查询分类异常", e);
            return null;
        }
    }

    // 聚合规格参数
    private List<Map<String, Object>> getSpecs(Long cid, QueryBuilder query) {
        try {
            // 根据分类查询规格
            List<SpecParam> params =
                    this.specificationClient.querySpecParams(null, cid, true, null);

            // 创建集合，保存规格过滤条件
            List<Map<String, Object>> specs = new ArrayList<>();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(query);

            // 聚合规格参数
            params.forEach(p -> {
                String key = p.getName();
                queryBuilder.addAggregation(AggregationBuilders.terms(key).field("specs." + key + ".keyword"));

            });

            // 查询
            Map<String, Aggregation> aggs = this.template.query(queryBuilder.build(),
                    SearchResponse::getAggregations).asMap();

            // 解析聚合结果
            params.forEach(param -> {
                Map<String, Object> spec = new HashMap<>();
                String key = param.getName();
                spec.put("k", key);
                StringTerms terms = (StringTerms) aggs.get(key);
                spec.put("options", terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString));
                specs.add(spec);
            });

            return specs;
        }catch (Exception e){
            log.error("[搜索服务]规格聚合出现异常：", e);
            return null;
        }
    }*/
}
