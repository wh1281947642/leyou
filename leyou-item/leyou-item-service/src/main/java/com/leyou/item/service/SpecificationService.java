package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <code>SpecificationService</code>
 * </p>
 *  规格参数 && 规格参数组
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/08 17:51
 */
@Service
public class SpecificationService {

    /***
     * 规格参数组
     */
    @Autowired
    private SpecGroupMapper specGroupMapper;

    /***
     * 规格参数
     */
    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id（cid）查询规格参数组
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/08 17:56
     * @param cid  分类id
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(record);
        return list;
    }

    /**
     * 根据条件查询规格参数
     * @description
     * @author huiwang45@iflytek.com
     * @date 2019/12/08 23:29
     * @param gid 规格参数组主键
     * @return
     */
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);
        List<SpecParam> list = specParamMapper.select(specParam);
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupsByCid(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = querySpecParams(null, cid, null, null);
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam param : specParams) {
            if (!map.containsKey(param.getGroupId())) {
                map.put(param.getGroupId(), new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);

        }
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getCid()));
        }

        return specGroups;
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParams(g.getId(), null, null, null));
        });
        return groups;
    }
}
