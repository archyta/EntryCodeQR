package cn.lanyue.cas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @author lanyue
 *
 * CAS APP
 */
@EnableCaching
@MapperScan(basePackages = {"cn.lanyue.cas.mapper"})
@SpringBootApplication
public class CasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasApplication.class, args);
    }

}
