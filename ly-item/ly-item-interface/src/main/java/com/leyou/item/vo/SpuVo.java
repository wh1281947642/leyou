package com.leyou.item.vo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import javax.persistence.Transient;
import java.util.List;

/**
 * <p>
 * <code>SpuBo</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/14 13:53
 */
public class SpuVo extends Spu {

    /***
     * 商品分类名称
     */
    private String cname;
    /***
     *  品牌名称
     */
    private String bname;
    /***
     *
     */
    private List<Sku> skus;
    /***
     *
     */
    private SpuDetail spuDetail;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }
}
