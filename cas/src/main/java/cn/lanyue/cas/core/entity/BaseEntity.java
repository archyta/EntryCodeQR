package cn.lanyue.cas.core.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entity支持类
 */
public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实体编号（唯一标识）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected String id;

    public BaseEntity() {

    }
    public BaseEntity(String id) {
        this();
        this.id = id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
//    /**
//     * 当前用户
//     */
//    @Transient
//    @JsonIgnore
//    protected User currentUser;

//    /**
//     * 自定义SQL（SQL标识，SQL内容）
//     */
//    @JsonIgnore
//    @Transient
//    protected Map<String, String> sqlMap;

//    /**
//     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
//     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
//     */
//    @ApiModelProperty(value = "isNewRecord",hidden = true)
//    @Transient
//    @JsonIgnore
//    protected boolean isNewRecord = false;

//    public User getCurrentUser() {
//        return currentUser;
//    }
//
//    public boolean getIsNewRecord() {
//        return isNewRecord;
//    }
//
//
//    @JsonIgnore
//    @XmlTransient
//    public Map<String, String> getSqlMap() {
//        if (sqlMap == null) {
//            sqlMap = Maps.newHashMap();
//        }
//        return sqlMap;
//    }

//    public void setCurrentUser(User currentUser) {
//        this.currentUser = currentUser;
//    }
//
//    public void setSqlMap(Map<String, String> sqlMap) {
//        this.sqlMap = sqlMap;
//    }

//    /**
//     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
//     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
//     */
//    public void setIsNewRecord(boolean isNewRecord) {
//        this.isNewRecord = isNewRecord;
//    }


    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity<?> that = (BaseEntity<?>) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";

}
