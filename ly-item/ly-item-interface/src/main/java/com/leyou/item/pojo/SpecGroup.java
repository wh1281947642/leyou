package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.net.UnknownServiceException;
import java.util.List;

/**
 * <p>
 * <code>SpecGroup</code>
 * </p>
 *  规格参数组
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/08 17:44
 */
@Table(name = "tb_spec_group")
@Data
public class SpecGroup {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    private Long cid;

    private String name;

    @Transient
    private List<SpecParam> params; // 该组下的所有规格参数集合
}
