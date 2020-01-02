package com.leyou.user.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;

/**
 * <p>
 * <code>SpecParam</code>
 * </p>
 *  规格参数
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/08 17:45
 */
@Data
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @Column(name = "`numeric`")  // numeric是关键字，反引号是转义为字符串
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;

}
