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
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

    public PageResult<Goods> search(SearchRequest request) {

        if (StringUtils.isBlank(request.getKey())){
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加分页，分页页码从零开始
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //执行查询，获取结果集
        NativeSearchQuery nativeSearchQuery = queryBuilder.build();
        Page<Goods> goodsPage = this.goodsRepository.search(nativeSearchQuery);
        return new PageResult<Goods>(goodsPage.getTotalElements(),(long) goodsPage.getTotalPages(),goodsPage.getContent());
    }
}
