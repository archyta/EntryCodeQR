package cn.lanyue.cas.config;

import cn.lanyue.cas.config.properties.UploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lanyue
 */
@ComponentScan
@Configuration
public class WebConfigurer implements WebMvcConfigurer {


    @Autowired
    private UploadProperties uploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/cas/v7/static/qrcode/**")
                .addResourceLocations("file:" + uploadConfig.getQrcodeUploadPath());
        registry.addResourceHandler("/cas/v7/static/estate/**") //门头照
                .addResourceLocations("file:" + uploadConfig.getHeadPhotoUploadPath());
        registry.addResourceHandler("/cas/v7/static/poster/guard/**") //门卫的海报
                .addResourceLocations("file:" + uploadConfig.getPosterGuardUploadPath());
        registry.addResourceHandler("/cas/v7/static/poster/owner/**") //小区住户的海报
                .addResourceLocations("file:" + uploadConfig.getPosterOwnerUploadPath());
    }

    /**
     * 配置cas项目所有
     * @see org.springframework.web.bind.annotation.RestController 注解的类前缀
     * @param configurer 路径匹配
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/cas/v7", c -> c.isAnnotationPresent(RestController.class));
    }
}
