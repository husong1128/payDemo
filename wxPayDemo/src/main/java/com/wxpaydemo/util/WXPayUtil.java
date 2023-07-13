package com.wxpaydemo.util;/**
 * @author husong
 * @create 2023-04-19 15:34
 */

import com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.*;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import com.wxpaydemo.config.WxPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *@author huxueyang
 * 用于微信支付，退款，查询等相关操作
 *@dare 2023/4/19 15:34
 */
@Component
@Slf4j
public class WXPayUtil {

   private final WXPayService wxPayService;
   public final NativePayService nativePayService;
   public final NotificationParser notificationParser;
   public final RefundService refundService;

    public WXPayUtil() {
        this.wxPayService = new WXPayService();
        this.nativePayService = wxPayService.getNativePayService();
        this.notificationParser  = wxPayService.getParse();
        this.refundService = wxPayService.getRefundService();
    }

    /**
     * 需要包含字段total，即订单金额(单位为分)，description，订单描述
     * 返回url：二维码网址，orderId：订单ID
     **/
    public Map<String ,Object> wxPay(Map<String, Object> map) {
        if (map==null||!CommonUtils.validateNoNull(map.get("total"),map.get("description"))){
            return ResultError("参数错误",null);
        }
        Map<String, Object> result = new HashMap<>();
//        订单号
        long orderId = Long.parseLong(UUID.randomUUID().toString().replace("-", ""));
        PrepayRequest request = new PrepayRequest();
        //设置金额描述等信息
        Amount amount = new Amount();
        amount.setTotal(Integer.parseInt(map.get("total")+""));
        amount.setCurrency("CNY");
        request.setAmount(amount);
        request.setNotifyUrl(WxPayConfig.notify_url);
        //设置描述
       request.setDescription(map.get("description")+"");
        request.setAppid(WxPayConfig.appid);
        request.setMchid(WxPayConfig.mch_id);
        request.setNotifyUrl(WxPayConfig.notify_url);
        request.setOutTradeNo(orderId+"");
        PrepayResponse response=new PrepayResponse();
        // 调用下单方法，得到应答
        try {
            response = nativePayService.prepay(request);
            //url为二维码网址
            HashMap<String, Object> payRes = new HashMap<>();
            payRes.put("url",response.getCodeUrl());
            payRes.put("orderId", orderId);
            return ResultOk(payRes);
        } catch (Exception e) {
            log.error("请求错误-->{}",e);
            return ResultError("请求错误",e);
        }
    }


    /**
     *
     * 微信订单查询。需要插入微信支付订单号，返回值result。
     **/
    public Map wxPayQueryWx(String orderId){
        if (orderId==null||orderId==""){
            return ResultError("参数错误",null);
        }
        QueryOrderByIdRequest queryRequest = new QueryOrderByIdRequest();
        queryRequest.setMchid(WxPayConfig.mch_id);
        //插入查询订单号
        queryRequest.setTransactionId(orderId);

        try {
            Transaction result = nativePayService.queryOrderById(queryRequest);
            return  ResultOk(result);
        } catch (ServiceException e) {
            // API返回失败, 例如ORDER_NOT_EXISTS
            Map<String, Object> queryRes = new HashMap<>();
            queryRes.put("errorCode",e.getErrorCode());
            queryRes.put("msg",e.getErrorMessage());
            return ResultError("查询失败",queryRes);
        }
    }

    /**
     * 商家订单查询。需要传入商家订单号，返回值result。
     **/
    public Map wxPayQueryMy(String orderId ){
        if (orderId==null||orderId==""){
            return ResultError("参数错误",null);
        }
        HashMap<String, Object> map = new HashMap<>();
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(WxPayConfig.mch_id);
        //为商家订单号
        queryRequest.setOutTradeNo(orderId);

        try {
            Transaction result = nativePayService.queryOrderByOutTradeNo(queryRequest);
            return ResultOk(result);
        } catch (ServiceException e) {
            // API返回失败, 例如ORDER_NOT_EXISTS
            Map<String, Object> queryRes = new HashMap<>();
            queryRes.put("errorCode",e.getErrorCode());
            queryRes.put("msg",e.getErrorMessage());
            return ResultError("查询失败",queryRes);
        }
    }

    /**
     *关单。需要传入商家订单号，返回值result。msg为错误提示
     * @return java.util.Map
     **/
    public Map closeWXOrder(String orderId){
        if (orderId==null||orderId==""){
            return ResultError("参数错误",null);
        }
        HashMap<String, Object> map = new HashMap<>();
        CloseOrderRequest queryRequest = new CloseOrderRequest();
        queryRequest.setMchid(WxPayConfig.mch_id);
        queryRequest.setOutTradeNo(orderId);
        try {
            nativePayService.closeOrder(queryRequest);
            return ResultOk("成功关单");
        } catch (ServiceException e) {
            Map<String, Object> closeRes = new HashMap<>();
            closeRes.put("errorCode",e.getErrorCode());
            closeRes.put("msg",e.getErrorMessage());
            return ResultError("关单失败",closeRes);
        }
    }

    /**
     * @Author huxueyang
     * 支付回调通知
    **/
     public Map<String, Object> wxPayNotify(HttpServletRequest request, HttpServletResponse response) {
            HashMap<String, Object> map = new HashMap<>();
        try {
            //获取回调参数中的信息
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SERIAL))
                    .nonce(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_NONCE))
                    .signature(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SIGNATURE))
                    .timestamp(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_TIMESTAMP))
                    .body(CommonUtils.getBody(request))
                    .build();
            //解密
            Transaction parse =notificationParser.parse(requestParam, Transaction.class);
            System.out.println(parse);
            Map<String, Object> stringObjectMap = ResultOk(null);
            //成功则响应SUCCESS
            stringObjectMap.put("code","SUCCESS");
            return stringObjectMap;
        } catch (IOException e) {
            return ResultError("系统异常",e);
        }
    }

    /**
     * @Author huxueyang
     * 退款回调通知
    **/
     public Map<String, Object> wxPayReFundNotify(HttpServletRequest request, HttpServletResponse response) {
            HashMap<String, Object> map = new HashMap<>();
        try {
            //获取回调参数中的信息
           RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SERIAL))
                    .nonce(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_NONCE))
                    .signature(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_SIGNATURE))
                    .timestamp(request.getHeader(WechatPayHttpHeaders.WECHAT_PAY_TIMESTAMP))
                    .body(CommonUtils.getBody(request))
                    .build();
            //解密
            RefundNotification parse = notificationParser.parse(requestParam, RefundNotification.class);
            System.out.println(parse);
            Map<String, Object> stringObjectMap = ResultOk(null);
            //成功则响应SUCCESS
            stringObjectMap.put("code","SUCCESS");
            return stringObjectMap;
        } catch (IOException e) {
            return ResultError("系统异常",e);
        }
    }




    /**
     * @Author huxueyang
     * 微信退款
     * 需要包含字段total，即订单总金额，refund:当次退款金额，单位分，orderId：订单id
     * 可含reason，退款原因
     **/
    public Map<String ,Object> wxPayRefund(Map<String, Object> map) {
        HashMap<String, Object> result = new HashMap<>();
        if (map==null||!CommonUtils.validateNoNull(map.get("orderId"),map.get("refund"),map.get("total"))){
            return ResultError("参数错误",null);
        }
        String orderId = map.get("orderId")+"";
        long refundId = Long.parseLong(UUID.randomUUID().toString().replace("-", ""));
        log.error("支付单号{}-->退款单号{}",orderId,refundId);
        CreateRequest createRequest = new CreateRequest();
        //创建退款金额信息
        AmountReq amountReq = new AmountReq();
        amountReq.setCurrency("CNY");
        amountReq.setRefund(Long.parseLong(map.get("refund")+""));
        amountReq.setTotal(Long.parseLong(map.get("total")+""));
        createRequest.setAmount(amountReq);
        createRequest.setOutRefundNo(refundId+"");
        createRequest.setOutTradeNo(orderId);
        createRequest.setReason(map.get("reason")+"");
        createRequest.setNotifyUrl(WxPayConfig.refund_notify_url);
        Refund refund = null;
        try {
            refund = refundService.create(createRequest);
            return ResultOk(refund);
        } catch (Exception e) {
            log.error("订单{}退款失败--->原因{}",orderId,e);
            return ResultError("退款失败",e);
        }
    }


    /**
     * @Author huxueyang
     * 查询订单退款状态，需要传入退款单号refundId
    **/
    public Map<String, Object> wxPayRefundQuery(Map<String, Object> map) {
        if (map==null||map.get("refundId")==null||map.get("refundId")==""){
            return ResultError("参数错误",null);
        }
        String refundId = map.get("refundId") + "";
        QueryByOutRefundNoRequest request = new QueryByOutRefundNoRequest();
        request.setOutRefundNo(refundId);
        Refund refund = null;
        try {
            refund = refundService.queryByOutRefundNo(request);
            return ResultOk(refund);
        } catch (Exception e) {
            log.error("退款订单{}查询状态失败--->原因{}",refundId,e);
            return ResultError("查询失败",e);
        }
    }
    public  Map<String, Object> ResultOk(Object data) {
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
