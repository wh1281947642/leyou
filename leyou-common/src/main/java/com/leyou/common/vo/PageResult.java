package com.leyou.common.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <p>
 * <code>PageResult</code>
 * </p>
 *
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/24 16:38
 */
@Data
public class PageResult<T> {
    /***
     * 总条数
     */
    private Long total;
    /***
     * 总页数
     */
    private Long totalPage;
    /***
     * 当前页数据
     */
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Long totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
