package com.wxpaydemo.config;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author xxs
 * @date 2023年04月19日 09:28
 */
@Component
@Data
public class WxPayConfig {
    public static String appid = "wx3f8ae89eec5c196e";
    public static String mch_id = "1632359732";
    public static  String doMain = "https://api.mch.weixin.qq.com";
    // API V3密钥
    public static String apiV3Key = "2yv1TtXxPHCMjgfR3FDWL9QVNuUqEc0d";
    public static String merchantSerialNumber = "564C7DBA323169AFB8BA3C5AE08A977FC2D13D4B";
    public static String notify_url = "https://jqlock.tianyihui.cc/wxpay/notify";
    public static String refund_notify_url = "https://jqlock.tianyihui.cc/wxpay/refundNotify";


    public static String privateKeyPath ="";

    public WxPayConfig() {
        try {
            privateKeyPath = new ClassPathResource("/key/apiclient_key.pem").getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
