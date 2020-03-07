package cn.lanyue.cas.core.entity;

import cn.lanyue.cas.core.utils.IdGen;
import cn.lanyue.cas.utils.Validator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * 数据Entity类
 *
 * @param <T>
 */
@Data
public abstract class DataEntity<T> extends BaseEntity<T> {

    private static final long serialVersionUID = 1L;

    @Column(name = "del_flag")
    @JsonIgnore
    protected String delFlag;

    @Column(name = "crt_time")
    protected Date crtTime;

    @Column(name = "crt_by")
    protected String crtBy;

  /*  @JsonIgnore
    @Column(name = "crt_name")
    protected String crtName;*/

    @Column(name = "upd_time")
    protected Date updTime;

  /*  @Column(name = "upd_user")
    @JsonIgnore
    protected String updUser;*/

    @Column(name = "upd_by")
    protected String updBy;

    public DataEntity() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public void preInsert() {
        if (Validator.isNullOrEmpty(id)) {
            this.id = IdGen.uuid();
        }
        this.delFlag = DEL_FLAG_NORMAL;
        this.updTime = new Date();
        this.crtTime = this.updTime;

        /*if (UserUtils.getUser().getId() != null) {
            this.createBy = UserUtils.getUser().getId();
            this.updateBy = UserUtils.getUser().getId();
        }*/
    }

    public void preUpdate() {
        this.delFlag = DEL_FLAG_NORMAL;
        this.updTime = new Date();
        /*if (UserUtils.getUser().getId() != null) {
            this.updateBy = UserUtils.getUser().getId();
        }*/
    }

    public DataEntity(String id) {
        super(id);
    }
}

