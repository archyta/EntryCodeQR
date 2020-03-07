package cn.lanyue.cas.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;


/**
 * 实体类相关工具类
 * 解决问题： 1、快速对实体的常驻字段，如：crtUser、updUser等值快速注入
 */
public class EntityUtils {
    /**
     * 快速将bean的crtUser、crtTime、updUser、updTime附上相关值
     *
     * @param entity
     * @param <T>
     */
    public static <T> void setCreatAndUpdatInfo(T entity) {
        setCreateInfo(entity);
        setUpdatedInfo(entity);
    }

    /**
     * 快速将bean的crtUser、crtTime附上相关值
     *
     * @param entity
     * @param <T>
     */
    public static <T> void setCreateInfo(T entity) {
        if (null == RequestContextHolder.getRequestAttributes()) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String name = "";
        String userId = "";
        Object id = ReflectionUtils.invokeGetter(entity, "id");
        if (request != null) {
            //name = StringUtils.trimToEmpty(request.getHeader("userName"));
            //name = URLDecoder.decode(name);
            userId = StringUtils.trimToEmpty(request.getHeader("userId"));
        }

        /*if (StringUtils.isBlank(name)) {
            //name = UserUtils.getUsername();
            name = null;
        }*/
        if (StringUtils.isBlank(userId)) {
            //userId = UserUtils.getUserID();
            userId = null;
        }
        if (id == null) {
            id = IdGen.uuid();
        }

        // 默认属性
        String[] fields = {"id", "crtBy", "crtTime"};
        Field field = ReflectionUtils.getAccessibleField(entity, "crtTime");
        // 默认值
        Object[] value = null;
        if (field != null && field.getType().equals(Date.class)) {
            value = new Object[]{id, userId, new Date()};
        }
        // 填充默认属性值
        setDefaultValues(entity, fields, value);
    }

    /**
     * 快速将bean的updUser、updTime附上相关值
     *
     * @param entity
     * @param <T>
     */
    public static <T> void setUpdatedInfo(T entity) {
        if (null == RequestContextHolder.getRequestAttributes()) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String name = "";
        String userId = "";
        if (request != null) {
            //name = StringUtils.trimToEmpty(request.getHeader("userName"));
            //name = URLDecoder.decode(name);
            userId = StringUtils.trimToEmpty(request.getHeader("userId"));
        }

        /*if (StringUtils.isBlank(name)) {
            //name = UserUtils.getUsername();
            name = null;
        }*/
        if (StringUtils.isBlank(userId)) {
            //id = UserUtils.getUserID();
            userId = null;
        }

        // 默认属性
        String[] fields = {"updBy", "updTime"};
        Field field = ReflectionUtils.getAccessibleField(entity, "updTime");
        Object[] value = null;
        if (field != null && field.getType().equals(Date.class)) {
            value = new Object[]{userId, new Date()};
        }
        // 填充默认属性值
        setDefaultValues(entity, fields, value);
    }

    /**
     * 依据对象的属性数组和值数组对对象的属性进行赋值
     *
     * @param entity
     * @param fields
     * @param value
     * @param <T>
     */
    private static <T> void setDefaultValues(T entity, String[] fields, Object[] value) {
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (ReflectionUtils.hasField(entity, field)) {
                ReflectionUtils.invokeSetter(entity, field, value[i]);
            }
        }
    }

    /**
     * 根据主键属性，判断主键是否值为空
     *
     * @param entity
     * @param field
     * @return 主键为空，则返回false；主键有值，返回true
     */
    public static <T> boolean isPKNotNull(T entity, String field) {
        if (!ReflectionUtils.hasField(entity, field)) {
            return false;
        }
        Object value = ReflectionUtils.getFieldValue(entity, field);
        return value != null && !"".equals(value);
    }
}
