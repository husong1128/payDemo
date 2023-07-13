package com.wxpaydemo;

import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;

class WxPayDemoApplicationTests {

    /** 商户号 */
    private static String merchantId = "1632359732";
    /** 商户API私钥路径 */
    //在resources目录下apiclient_key.pem文件
    private static String privateKeyPath = "key/apiclient_key.pem";
    /** 商户证书序列号 */
    private static String merchantSerialNumber = "564C7DBA323169AFB8BA3C5AE08A977FC2D13D4B";
    /** 商户证书路径 */
    private static String wechatPayCertificatePath = "key/output.pem";
    /** APPID */
    private static String appID = "wx3f8ae89eec5c196e";
    /** 支付成功回调地址 */
    private static String notifyUrl = "https://www.baidu.com";
//D:\project\demo\wxPayDemo\target\classes\key\output.pem
    private static NativePayService service;


    public static void main(String[] args) {
        prepareWXPay("测试商品", 1, "123456789");
    }

    /**
     * 微信预支付
     */
    public static String prepareWXPay(String title, float money, String outTradeNo) {
        // 初始化商户配置
        if (service == null) {
            privateKeyPath = WxPayDemoApplicationTests.class.getClassLoader().getResource(privateKeyPath).getPath();
            wechatPayCertificatePath = WxPayDemoApplicationTests.class.getClassLoader().getResource(wechatPayCertificatePath).getPath();
            System.out.println(privateKeyPath);
            System.out.println(wechatPayCertificatePath);
            RSAConfig config =
                    new RSAConfig.Builder()
                            .merchantId(merchantId)
                            .privateKeyFromPath(privateKeyPath)
                            .merchantSerialNumber(merchantSerialNumber)
                            .wechatPayCertificatesFromPath(wechatPayCertificatePath)
                            .build();
            // 初始化服务
            service = new NativePayService.Builder().config(config).build();
        }
        PrepayRequest request = new PrepayRequest();
        request.setAppid(appID);
        request.setMchid(merchantId);
        request.setOutTradeNo(outTradeNo);
        request.setDescription(title);
        request.setNotifyUrl(notifyUrl);
        Amount amount = new Amount();
        amount.setTotal(Integer.parseInt(String.valueOf((int) (money * 100))));
        amount.setCurrency("CNY");
        request.setAmount(amount);
        // 调用接口
        PrepayResponse resp = service.prepay(request);
        if (resp != null) {
            return resp.getCodeUrl();
        }
        return null;
    }

}
