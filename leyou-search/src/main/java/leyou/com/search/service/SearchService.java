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
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.Build;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
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
    private GoodsRepository goodsRepository;

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
        goods.setCid3(spu.getCid3());
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

    /**
     * 根据key查询分页集合
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/28 12:07
     * @param
     * @return
     */
    public SearchResult search(SearchRequest request) {

        if (StringUtils.isBlank(request.getKey())){
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        //MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        //添加分页，分页页码从零开始
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //添加分类的品牌的聚合
        String categoryAggName = "categorys";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //执行查询，获取结果集
        NativeSearchQuery nativeSearchQuery = queryBuilder.build();
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(nativeSearchQuery);
        //获取聚合结果集并解析
        List<Category> categories = getCategoryAggResult( goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult( goodsPage.getAggregation(brandAggName));
        List<Map<String,Object>> specs =null;
        //判断是否是一个分类，只有一个分类是才做规格参数聚合
        if(!CollectionUtils.isEmpty(categories) && categories.size()==1){
            //规格参数聚合
           specs = getParamAggResult((long)categories.get(0).getId(),basicQuery);

        }
        return new SearchResult(goodsPage.getTotalElements(),(long) goodsPage.getTotalPages(),goodsPage.getContent(),categories,brands,specs);
    }

    /**
     * 构建bool查询
     *
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/12/29 00:00
     * @param
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给布尔查询添加基本的查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //添加过滤条件
        //获取用户选择的过滤信息
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            //品牌
            if((!("brandId".equals(key)) && (!("cid3".equals(key))))){
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 解析分类的聚合结果集
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/28 15:21
     * @param
     * @return
     */
    private List<Category> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取聚合中的桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //转化成List<Category>
        List<Category> categoryList = buckets.stream().map(bucket -> {
            //获取桶中分类id
            long cid = bucket.getKeyAsNumber().longValue();
            //根据分类id查询分类名称
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(cid));
            Category category = new Category();
            category.setId(cid);
            category.setName(names.get(0));
            return category;
        }).collect(Collectors.toList());
        return categoryList;
    }

    /**
     * 解析品牌的聚合结果集
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/28 15:20
     * @param
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取聚合中的桶
        List<LongTerms.Bucket> buckets = terms.getBuckets();
        //转化成List<Brand>
        List<Brand> brandList = buckets.stream().map(bucket -> {
            long bid = bucket.getKeyAsNumber().longValue();
            Brand brand = this.brandClient.queryBrandById(bid);
            return brand;
        }).collect(Collectors.toList());
        return brandList;
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @description TODO
     * @author huiwang45@iflytek.com
     * @date 2019/12/28 16:45
     * @param
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>> getParamAggResult(long cid, BoolQueryBuilder basicQuery) {
        //构建自定义查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParams(null, cid, null, true);
        //添加规格参数聚合
        specParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
        });
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询,获取聚合结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        //解析聚合结果集 key-聚合名称（规格参数名） value-聚合对象
        List<Map<String, Object>> specs = new ArrayList<Map<String, Object>>();
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化map {key：规格参数名，options：聚合的规格参数值}
            Map<String, Object> map = new HashMap<>();
            map.put("k",entry.getKey());
            //获取聚合
            StringTerms terms = (StringTerms)entry.getValue();
            //获取聚合中的桶
            List<StringTerms.Bucket> buckets = terms.getBuckets();
            List<String> options = buckets.stream().map(bucket -> {
                return bucket.getKeyAsString();
            }).collect(Collectors.toList());
            map.put("options",options);
            specs.add(map);
        }
        return specs;
    }
}
