package leyou.com.search.repository;

import leyou.com.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * <p>
 * <code>GoodsRepository</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/27 11:03
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long>{
}
