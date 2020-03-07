package cn.lanyue.cas.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "preread")
@Getter
@Setter
public class UploadProperties {

    /**
     * 二维码上传路径(门卫、住户)
     */
    private String qrcodeUploadPath;

    /**
     * 小区门头照
     */
    private String headPhotoUploadPath;

    /**
     * 生成门卫海报的路径
     */
    private String posterGuardUploadPath;

    /**
     * 生成住户的海报路径
     */
    private String posterOwnerUploadPath;





}
