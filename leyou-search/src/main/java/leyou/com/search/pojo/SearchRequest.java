package leyou.com.search.pojo;

import java.util.Map;

/**
 * <p>
 * <code>SearchRequest</code>
 * </p>
 *  用于接收json数据的对象
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/27 16:02
 */
public class SearchRequest {
    /***
     * 搜索条件
     */
    private String key;
    /***
     * 当前页
     */
    private Integer page;

    private Map<String,Object> filter;
    /***
     * 每页大小，不从页面接收，而是固定大小
     */
    private static final Integer DEFAULT_SIZE = 20;
    /***
     * 默认页
     */
    private static final Integer DEFAULT_PAGE = 1;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }
}
