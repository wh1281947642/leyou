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
    private Long total;     // 总条数
    private Long totalPage; // 总页数
    private List<T> items;  // 当前页数据

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
