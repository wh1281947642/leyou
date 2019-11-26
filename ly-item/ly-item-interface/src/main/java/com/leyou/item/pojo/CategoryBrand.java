package com.leyou.item.pojo;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * <code>CategoryBrand</code>
 * </p>
 *  商品管理 分类管理 关联表
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/25 17:37
 */
@Table(name="tb_category_brand")
@Data
public class CategoryBrand {

  @Id
  private long categoryId;
  @Id
  private long brandId;

}
