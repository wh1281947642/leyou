package com.leyou.user.api;

import com.leyou.user.pojo.SpecGroup;
import com.leyou.user.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * <code>SpecificationApi</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/26 15:00
 */
@RequestMapping("spec")
public interface SpecificationApi {

    /**
     * 根据条件查询规格参数
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/08 23:29
     * @param gid 规格参数组主键
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> querySpecParams(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic);

    /**
     * 根据分类id（cid）查询规格参数组和规格参数
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/30 19:36
     * @param
     * @return
     */
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long cid);
}
