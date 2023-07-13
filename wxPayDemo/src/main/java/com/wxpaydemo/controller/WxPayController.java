package com.wxpaydemo.controller;


import com.wxpaydemo.util.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xxs
 * @date 2023年04月18日 21:11
 */
@Controller
@RequestMapping("/wxpay")
@ResponseBody
public class WxPayController {
    @Resource
    private WXPayUtil wxPayUtil;

//    得到二维码
    @PostMapping("/wxPay")
    public Map<String, Object> pay(@RequestBody Map<String,Object> map) {
        return wxPayUtil.wxPay(map);
    }

    //    查询微信订单
    @PostMapping("/wxPayQueryWx")
    public Map<String, Object> queryWXOrderId(@RequestBody Map<String,Object> map) {
        if (map==null||map.get("orderId")==null||map.get("orderId")==""){
            Map<String, Object> res = new HashMap<>();
            return wxPayUtil.ResultError("参数错误",null);
        }
        Map<String, Object> res = wxPayUtil.wxPayQueryWx(map.get("orderId")+"");
        return res;
    }
//    查询商户订单
    @PostMapping("/wxPayQueryMy")
    public Map<String, Object> queryMyOrderId(@RequestBody Map<String,Object> map) {
        if (map==null||map.get("orderId")==null||map.get("orderId")==""){
            Map<String, Object> res = new HashMap<>();
            return wxPayUtil.ResultError("参数错误",null);
        }
        Map<String, Object> res = wxPayUtil.wxPayQueryMy(map.get("orderId")+"");
        return res;
    }
    //    关单
    @PostMapping("/closeWXOrder")
    public Map<String, Object> closeOrderId(@RequestBody Map<String,Object> map) {
        if (map==null||map.get("orderId")==null||map.get("orderId")==""){
            Map<String, Object> res = new HashMap<>();
            return wxPayUtil.ResultError("参数错误",null);
        }
        Map<String, Object> res = wxPayUtil.closeWXOrder(map.get("orderId")+"");
        return res;
    }

//    支付回调
    @PostMapping("/wxPayNotify")
    public Map<String, Object> notify(HttpServletRequest request, HttpServletResponse response) {
        return wxPayUtil.wxPayNotify(request,response);
    }
//    退款回调
    @PostMapping("/wxPayReFundNotify")
    public Map<String, Object> refundNotify(HttpServletRequest request, HttpServletResponse response) {
        return wxPayUtil.wxPayReFundNotify(request,response);
    }

    /**
     * @Author huxueyang
     * @date 2023/4/19 15:50
     * 需要传入原微信支付订单号或者原商户订单号
     * @return java.util.Map<java.lang.String,java.lang.Object>
    **/
    @PostMapping("/wxPayRefund")
    public Map<String, Object> refund(@RequestBody Map<String,Object> map) {
         return wxPayUtil.wxPayRefund(map);
    }


    /**
     * @Author huxueyang
     * 退款状态查询
     * 需要传入退款单号
     * @return java.util.Map<java.lang.String,java.lang.Object>
     **/
    @PostMapping("/wxPayRefundQuery")
    public Map<String, Object> refundQuery(@RequestBody Map<String,Object> map) {
        return wxPayUtil.wxPayRefundQuery(map);
    }
}
