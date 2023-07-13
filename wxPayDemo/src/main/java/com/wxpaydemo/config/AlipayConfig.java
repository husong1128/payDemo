package com.wxpaydemo.config;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 */
@Data
public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id ="2021000122619966";
	// 支付宝网关,正式环境为 https://openapi.alipay.com/gateway.do
	public static String serverUrl ="https://openapi.alipaydev.com/gateway.do";

	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCYI/ZOVRZihKeNvb/5sH9GxIazNk/ahP6oL9QClEiW80GBcerih2GQgxzO1sXmuuptqvo6xxriXeHRLoIy9mU4EWGg/9XFGLQrWS6ijUTbsS7QBow8gigvuESIxC3xRMk2oEVnWotFR8o+fr3FzG/YFjXkYw6/4iLdrtYhGoVRwKJtzmgL1yqNj7WBlam4NBb/Z7q/A/zZlXbEn2oSuzXyhI7CvNQoAICPf1IASb1H0iQew45A5tLCwsqPaiOvMAbl1PCX9PnG3NXf/eoBOWHyHh3/cJRKkday0YUvCYcgcvbYBI6OjWI5omz3GbwAoB/Q7Su+QHM/a5aftp2B1oz7AgMBAAECggEAeYZ4Tze+KrGlmR3Rg+QMtn1yzyH6BSWouLWDlw/VL+ZdE8XoBkx3jVR9pWZ/K/9wFGu3d03lVp4BK/MHgye30GJp5o2Uttw1xk51ZtiLASV1rCsex5J8ASoibFCwFgGI7D53UchMaOmgTe01LxNO339WSvkpJfK/QO5QEp85kQOfViyzxcRCb3JB+HeqjeiT0HjtuSjTowsCcpwhy22BIu2hbu6RVrLovjqlYj8ITJRZJ+w1J1r4Fi3ECpZRFVvUDdv7u3N1A3Qy7NiG3q1YegRqhhIIbcZZ3bdP07z/22REa5cXqsFPv16JOhwGEW3gyOXGCYHBhJr0r3zJM1ZUcQKBgQDUfW6HxzaaFjxvO7Xo37sGrtUA+6nXqWjdo/6ps9aj1a1uGudYFZBnfXB5UKnYCbTkn6/U5YIUmK9a6pWgT7pJSp7FsWFARzxE/bb/x/6xMuKyP2PS4914wtomORZgFQb3T4vcxsqahk6/qKQ4KJAqnqZAs5LMb8py8YiQaQfecwKBgQC3Sw3pJYAlVPLDZXjE8Nmly4FyiT0QXHjM9Mxy5EDpIzVmb9IocARAPuZVIkAJ9egNmYvVT9UVorlhh2OYCSq6HH1+dBcg3oRGItiCTzv+0DJqUKHlY7e6ci2irQW0B14DUZ/uv3USSwpwBgX6KVkyt4dkPs34n45Qu24f9AAtWQKBgAv0To6W94xtqdK8Mjy6sYPrvBIFFoYKgtrNSht0AaRP9iTWoKNE+03HMdexYQzFZwbhsPuyOcI84Miln+tX8OwEpbALjaQXHd3nxC7Zqot/iF9sz4nixefwVmOGHKslI7IwKHcwO9+7pJyFuU9rgMjVChdjwuN7epFC42L0dbvfAoGANzEccTXfjl4z825cnUDA6NGMmwaEIqcVoPGdloLwoML/U2ccaxy/yvClvsDfc6BeTaoMlyDA0xD6rUltTdyExyHJEAi45ibawZvAggyFzka2x5icB0ytiDDVWQSskPce8fTkKanxAJ8j93N43UkSGNl2pVLcBp9U5S5rDKgWXZECgYAJXxN4+ghVpPZNc+q6KHBfroeD3AESw+3Q3hwFFuiBjO/7vjFGvYZfcsHlJJBxqN9RRglzUojpYcgr3dq49e5aVKm6ttdMBl5Lcr3I8fzcI8KCJoWkLBN4/lJRXS8bbvUsPuHSwR1/r2j35Usc13QXf4ck4IBQpsRaQDShuDGq1w==";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiVIBgnaYrnrqRDh08VxMGeDWrU/BSr2wEJ6E7Olmi0mIZUMm2IR5M9sedM+mbqh6QJSa01lPjZarG/GqVjYfnXzIflwE1JoECnQV+xllbhEUbypvEByviBL+QVMdH6LvRuYh2tyTr09iDvhNM/tyUtKCMG1lQsMhin0ecS9905K5tSPD0NqSaB+0cWqYD8mBVCmkg0jnQy+7hc/bS1a0wMtkdYyuJH1EejBSNrRFW8DVSuxMjOzHs5gMPGfUo7CGM4u5VR5JxIgh7Oc7VFVmqqKjmGty1N2l7lAdeqcgjBrs+kocDtiGmmM5vGS4D7A5DTnq7SdCAg3lukJ5ZyJyaQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url ="https://jqlock.tianyihui.cc/zfbpay/zfbNotify";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "https://jqlock.tianyihui.cc/";

	// 签名方式
	public static String sign_type ="RSA2";
	
	// 字符编码格式
	public static String charset ="utf-8";
	
	// 支付宝网关
	public static String gatewayUrl ="https://openapi.alipay.com/gateway.do";

}

