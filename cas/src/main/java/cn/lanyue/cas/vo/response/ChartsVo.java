package cn.lanyue.cas.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author 
 * @Description TODO
 * @Date 2020/2/19 15:44
 */
@Getter
@Setter
public class ChartsVo implements Comparable<ChartsVo>{

    @JsonFormat(pattern = "MM/dd")
    private LocalDate date;

    private Long value;



    @Override
    public int compareTo(ChartsVo o) {
        return this.date.compareTo(o.getDate());
    }
}
