package com.leyou.item.web;

import com.leyou.user.pojo.SpecGroup;
import com.leyou.user.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * <code>SpecificationController</code>
 * </p>
 *  规格参数 && 规格参数组
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/08 17:52
 */
@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    SpecificationService specificationService;

    /**
     * 根据分类id（cid）查询规格参数组
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/08 17:56
     * @param cid  分类id
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.queryGroupsByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     * 根据分类id（cid）查询规格参数组和规格参数
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/30 19:36
     * @param
     * @return 
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroupList = this.specificationService.queryGroupsWithParam(cid);
        if(CollectionUtils.isEmpty(specGroupList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroupList);
    }


    /**
     * 根据条件查询规格参数
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/08 23:29
     * @param gid 规格参数组主键
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParams(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic){
        List<SpecParam> specParams = specificationService.querySpecParams(gid, cid, searching, generic);
        if (CollectionUtils.isEmpty(specParams)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);
    }

    /**
     * 根据分类查询规格组及组内分类
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(specificationService.querySpecsByCid(cid));
    }


}
