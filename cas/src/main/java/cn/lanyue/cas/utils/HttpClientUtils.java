package cn.lanyue.cas.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * @author 
 * @Description http请求工具类
 * @Date 2020/2/20 9:42
 */
@Slf4j
public class HttpClientUtils {

    public static final String DEFAULT_CHARSET = "UTF-8";

    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
                                                                     .setConnectTimeout(5000)
                                                                     .setConnectionRequestTimeout(5000)
                                                                     .setSocketTimeout(30000).build();


    public static final String getString(String url, String contentType, Map<String,String> headers) throws Exception {

        if (StringUtils.isEmpty(url)) {
            throw new NullPointerException("URL为空");
        }

        log.debug("[HTTP]发送数据: url={}", url);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet method = new HttpGet(url);

        if (MapUtils.isNotEmpty(headers)) {
            for (String key : headers.keySet()) {
                method.addHeader(key, headers.get(key));
            }
        }

        method.setConfig(defaultRequestConfig);

        method.addHeader("Content-Type", contentType);

        String responseContent ;

        try {
            // 执行请求
            HttpResponse response = httpClient.execute(method);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // 成功
                responseContent = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);

                log.debug("[HTTP]响应数据: {}, {}", statusCode, responseContent);

                return responseContent;

            } else {
                // 失败
                responseContent = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);

                log.error("[HTTP]响应数据: {}, {}", statusCode, responseContent);

                return null;
            }
        } catch (Exception ex) {

            log.error("***HTTP请求调用出现异常: " + url, ex);

            throw ex;

        } finally {
            if (method != null) {
                // 释放连接
                method.releaseConnection();
            }
            if (httpClient != null) {
                // 关闭实例
                httpClient.close();
            }
        }
    }

}
