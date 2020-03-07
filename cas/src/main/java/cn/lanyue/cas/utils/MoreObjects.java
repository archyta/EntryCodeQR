package cn.lanyue.cas.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Auther: lanyue
 * @Date: 20-1-19
 * @Description: JDK Objects增强类
 */
@UtilityClass
public class MoreObjects {


    /**
     * 集合的拷贝
     * 通常用于相同属性的对象之间的拷贝
     * @param sourceList 拷贝源
     * @param targetClass 目标类class
     * @param <T> 泛型
     * @return 目标类的集合
     */
    public static <T> List copyList(List<? extends Object> sourceList, Class<T> targetClass) {

        if (Validator.isNullOrEmpty(sourceList)) {
            return Collections.emptyList();
        }
        
        List<T> targets = Lists.newArrayListWithCapacity(sourceList.size());
        for (Object source : sourceList) {
            T target;
            try {
                target = targetClass.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            BeanUtils.copyProperties(source, target);
            targets.add(target);
        }

        return targets;
    }

}
