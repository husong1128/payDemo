package com.wxpaydemo.util;/**
 * @author husong
 * @create 2023-04-20 9:24
 */

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.wxpaydemo.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *@author huxueyang
 *@dare 2023/4/20 9:24
 */
@Component
@Slf4j
public class ZFBPayUtil {
   private AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.serverUrl,AlipayConfig.app_id,AlipayConfig.merchant_private_key,"json",AlipayConfig.charset,AlipayConfig.alipay_public_key,AlipayConfig.sign_type);
    /**
     * @Author huxueyang
     * 支付宝下单接口
     * 必填：total_amount（金额），subject（标题）
     * 可选：time_expire（过期时间）等
     * @return
    **/
    public Map<String, Object> zfbPay(Map<String,Object> map){
        if (map==null||!CommonUtils.validateNoNull(map.get("total_amount"),map.get("subject"))){
            return ResultError("参数错误",null);
        }
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置回调通知地址和支付后返回地址
        request.setNotifyUrl(AlipayConfig.notify_url);
        request.setReturnUrl(AlipayConfig.return_url);
        JSONObject bizContent = new JSONObject();
        //支付单号
        long orderId = Long.parseLong(UUID.randomUUID().toString().replace("-", ""));
//       //此两项来自自己
        bizContent.put("out_trade_no", orderId);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //copy传入的其他参数
        CommonUtils.copyMap(map,bizContent);
        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            log.error("创建支付宝订单失败--->"+e);
            return ResultError("创建支付宝订单失败失败",e);
        }
        if(response.isSuccess()){
            log.info("创建支付宝订单成功{}--->",orderId);
            Map<String, Object> stringObjectMap = ResultOk(response.getBody());
            stringObjectMap.put("orderId",orderId);
            return stringObjectMap;
        } else {
            Map<String, Object> resultError = ResultError("创建支付宝订单失败失败", response);
            log.info("创建支付宝订单失败--->"+resultError);
            return resultError;
        }
    }
    /**
     * @Author huxueyang
     * 支付宝下单查询接口
     * 必填：trade_no（支付宝交易号），out_trade_no（商家订单号）（有一个即可）
     * @return
    **/
    public Map<String, Object> zfbPayQuery(Map<String,Object> map){
        if (map==null||!CommonUtils.validateOne(map.get("trade_no"),map.get("out_trade_no"))){
            return ResultError("请传入查询单号",null);
        }
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        boolean flag=false;
        //拼接参数
        String bizContent="{";
        if (!(map.get("trade_no")==null||map.get("trade_no")=="")){
            flag=true;
            bizContent=bizContent+"\"trade_no\":\""+map.get("trade_no")+"\"";
        }
        if (!(map.get("out_trade_no")==null||map.get("out_trade_no")=="")){
            if (flag) {
                bizContent=bizContent+",";
            }

            bizContent=bizContent+"\"out_trade_no\":\""+map.get("out_trade_no")+"\"";
        }
        bizContent=bizContent+"  ,\"query_options\":[" +
                "    \"trade_settle_info\"" +
                "  ]" +
                "}";
        request.setBizContent(bizContent);
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("查询支付宝订单失败--->"+e);
            return ResultError("查询支付宝订单失败失败",e);
        }
        if(response.isSuccess()){
            Map<String, Object> resultOk = ResultOk(response);
            log.info("查询支付宝订单成功--->",resultOk);
            return resultOk;
        } else {

            Map<String, Object> resultError = ResultError("查询支付宝订单失败失败", response);
            log.info("查询支付宝订单失败{}",resultError);
            return resultError;
        }
    }    /**
     * @Author huxueyang
     * 支付宝退款接口
     * 必填：trade_no（支付宝交易号），out_trade_no（商家订单号）（有一个即可）  refund_amount（退款金额，单位元）
     * 可选 out_request_no（退款请求号。标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传）
     * @return
    **/
    public Map<String, Object> zfbRefund(Map<String,Object> map){
        //校验
        if (map==null||!CommonUtils.validateOne(map.get("out_trade_no"),map.get("trade_no"))||!CommonUtils.validateOne(map.get("refund_amount"))){
            return ResultError("参数错误",null);
        }
        //构造请求参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        if (CommonUtils.validateNoNull(map.get("out_trade_no"))){
            bizContent.put("out_trade_no", map.get("out_trade_no"));
        }
        if (CommonUtils.validateNoNull(map.get("trade_no"))){
            bizContent.put("trade_no", map.get("trade_no"));
        }
        if (CommonUtils.validateNoNull(map.get("out_request_no"))){
            bizContent.put("out_request_no", map.get("out_request_no"));
        }
        bizContent.put("refund_amount", map.get("refund_amount"));
        CommonUtils.copyMap(map,bizContent);
        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = null;
        //发请求
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("支付宝订单退款失败--->"+e);
            return ResultError("支付宝订单退款失败",e);
        }
        if(response.isSuccess()){
            Map<String, Object> resultOk = ResultOk(response);
            log.info("支付宝订单退款成功商户单号{},支付宝单号{}，请求号{},退款金额,已退款金额{}",map.get("out_trade_no"),map.get("trade_no"),map.get("out_request_no"),map.get("refund_amount"),response.getRefundFee());
            return resultOk;
        } else {
            Map<String, Object> resultError = ResultError("支付宝订单退款失败", response);
            log.info("支付宝订单退款失败商户单号{},支付宝单号{}，请求号{},退款金额{}",map.get("out_trade_no"),map.get("trade_no"),map.get("out_request_no"), map.get("refund_amount"));
            log.info("支付宝订单退款失败信息",resultError);
            return ResultError("支付宝订单退款失败",response);
        }
    }
    /* @Author huxueyang
     * @date 2023/4/20 9:27
     * 支付宝退款查询接口
     * 必填：trade_no（支付宝交易号），out_trade_no（商家订单号）（有一个即可）
     * out_request_no（退款请求号。请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的商户订单号）
    **/
    public Map<String, Object> zfbRefundQuery(Map<String,Object> map){
        //校验
        if (!CommonUtils.validateOne(map.get("out_trade_no"),map.get("trade_no"))||!CommonUtils.validateOne(map.get("out_request_no"))){
            return ResultError("参数错误",null);
        }
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        if (CommonUtils.validateNoNull(map.get("out_trade_no"))){
            bizContent.put("out_trade_no", map.get("out_trade_no"));
        }
        if (CommonUtils.validateNoNull(map.get("trade_no"))){
            bizContent.put("trade_no", map.get("trade_no"));
        }
        bizContent.put("out_request_no", map.get("out_request_no"));
        CommonUtils.copyMap(map,bizContent);

        request.setBizContent(bizContent.toString());
        AlipayTradeFastpayRefundQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("支付宝退款订单查询失败--->"+e);
            return ResultError("支付宝退款订单查询失败",e);
        }
        if(response.isSuccess()){
            Map<String, Object> resultOk = ResultOk(response);
            log.info("支付宝退款订单查询成功--->"+resultOk);
            return ResultOk(response.getBody());
        } else {
            Map<String, Object> resultError = ResultError("支付宝退款订单查询失败", response);
            log.info("支付宝退款订单查询失败--->"+resultError);
            return resultError;
        }
    }
    /* @Author huxueyang
     * @date 2023/4/20 9:27
     * 支付宝关单
     * 必填：trade_no（支付宝交易号），out_trade_no（商家订单号）（有一个即可）
    **/
    public Map<String, Object> closeZfbOrder(Map<String,Object> map){
        //校验
        if (!CommonUtils.validateOne(map.get("out_trade_no"),map.get("trade_no"))){
            return ResultError("参数错误",null);
        }
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        if (CommonUtils.validateNoNull(map.get("out_trade_no"))){
            bizContent.put("out_trade_no", map.get("out_trade_no"));
        }
        if (CommonUtils.validateNoNull(map.get("trade_no"))){
            bizContent.put("trade_no", map.get("trade_no"));
        }

        CommonUtils.copyMap(map,bizContent);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("支付宝订单关单失败--->"+e);
            return ResultError("支付宝订单关单失败",e);
        }
        if(response.isSuccess()){
            Map<String, Object> resultOk = ResultOk(response.getBody());
            log.info("支付宝订单关单成功-->{}",resultOk);
            return resultOk;
        } else {
            Map<String, Object> resultError = ResultError("支付宝订单关单失败", response);
            log.info("支付宝订单关单失败--->{}",resultError);
            return resultError;
        }
    }

    /* @Author huxueyang
     * 支付宝回调通知（支付回调退款回调）
     **/
    public Map<String, Object> zfbNotify(Map<String, String> params){

        Map<String,Object> resultMap = new HashMap<>();
        //异步通知验签
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    AlipayConfig.alipay_public_key,
                    AlipayConfig.charset,
                    AlipayConfig.sign_type
            );
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if(!signVerified){
            //验签失败则记录异常日志，并在response中返回failure.
            resultMap.put("status", 500);
            return resultMap;
        }
        // 验签成功后
        String appIdProperty = AlipayConfig.app_id;
        String tradeStatus = params.get("trade_status");
        resultMap.put("appIdProperty",appIdProperty);
        resultMap.put("tradeStatus",tradeStatus);
        resultMap.put("status", 200);
        return resultMap;
    }

    public Map<String, Object> ResultOk(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("status",200);
        map.put("result",data);
        return map;
    }
    public  Map<String, Object> ResultError(String message,Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("status",500);
        map.put("message",message);
        map.put("result",data);
        return map;
    }
}
