package com.wxpaydemo.util;/**
 * @author husong
 * @create 2023-04-19 11:27
 */

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import com.wxpaydemo.config.WxPayConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *@author huxueyang
 * 这些为单实例
 * 用于向微信发请求
 *@dare 2023/4/19 11:27
 */
@Data
@Slf4j
public class WXPayService {

    //支付对象
    public   final NativePayService nativePayService;
    //解析返回数据对象
    public   final NotificationParser  parse;
    //退款对象
    public   final RefundService refundService;
    public WXPayService() {
//        WxPayConfig wxPayConfig = new WxPayConfig();
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(WxPayConfig.mch_id)
                        .privateKeyFromPath(WxPayConfig.privateKeyPath)
                        .merchantSerialNumber(WxPayConfig.merchantSerialNumber)
                        .apiV3Key(WxPayConfig.apiV3Key)
                        .build();

        // 构建service
        this.nativePayService = new NativePayService.Builder().config(config).build();
        this.parse = new NotificationParser((NotificationConfig)config);
        this.refundService =  new RefundService.Builder().config(config).build();
    }
}
