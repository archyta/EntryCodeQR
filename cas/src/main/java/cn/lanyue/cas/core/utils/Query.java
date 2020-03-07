package cn.lanyue.cas.core.utils;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询参数
 */
@Data
public class Query extends LinkedHashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    //当前页码
    private int page = 1;
    //每页条数
    private int limit = 10;

    public Query(Map<String, Object> params) {
        //分页参数
        if (params.get("page") != null) {
            String pageStr = params.get("page").toString();
            if (StringUtils.isNumeric(pageStr)){
                this.page = Integer.valueOf(pageStr);
            }
        }
        if (params.get("limit") != null) {
            String limitStr = params.get("limit").toString();
            if (StringUtils.isNumeric(limitStr)) {
                this.limit = Integer.valueOf(limitStr);
            }
        }
        params.remove("page");
        params.remove("limit");
        this.putAll(params);
    }
}
