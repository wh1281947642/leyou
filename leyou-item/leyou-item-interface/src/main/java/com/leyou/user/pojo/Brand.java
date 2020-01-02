package com.leyou.user.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * <code>Brand</code>
 * </p>
 *  商品管理
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 17:06
 */
@Data
@Table(name = "tb_brand")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;// 品牌名称
    private String image;// 品牌图片
    private Character letter;

}
