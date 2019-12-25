package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * <code>Spu</code>
 * </p>
 * 标准产品单位，商品集
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/14 13:42
 */
@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    /***
     *
     */
    private Long id;
    /***
     *
     */
    private Long brandId;
    /***
     * 1级类目
     */
    private Long cid1;
    /***
     * 2级类目
     */
    private Long cid2;
    /***
     * 3级类目
     */
    private Long cid3;
    /***
     * 标题
     */
    private String title;
    /***
     * 子标题
     */
    private String subTitle;
    /***
     * 是否上架
     */
    private Boolean saleable;
    /***
     * 是否有效，逻辑删除用
     */
    private Boolean valid;
    /***
     * 创建时间
     */
    private Date createTime;
    /***
     * 最后修改时间
     */
    private Date lastUpdateTime;
}
