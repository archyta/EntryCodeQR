package cn.lanyue.cas.core.utils;

import cn.lanyue.cas.core.msg.TableResultResponse;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 
 * @Description 内存分页
 * @Date 2020/2/24 20:45
 */
@UtilityClass
public class MemPageHelper{


    /**
     * 内存分页
     * @param lists 需要分页的列表
     * @param query 分页参数
     * @return 分页的列表
     */
    public TableResultResponse start(List<?> lists, Query query) {
        if ((query.getPage()-1) * query.getLimit() > lists.size()) {
            return new TableResultResponse(lists.size(), Collections.emptyList());
        }
        int total = lists.size();
        lists = lists.stream().skip((query.getPage()-1) * query.getLimit()).limit(query.getLimit()).collect(Collectors.toList());
        return new TableResultResponse(total, lists );
    }


}
