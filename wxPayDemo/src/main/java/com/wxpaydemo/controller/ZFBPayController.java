package com.wxpaydemo.controller;/**
 * @author husong
 * @create 2023-04-20 9:24
 */

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.wxpaydemo.config.AlipayConfig;
import com.wxpaydemo.util.ZFBPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *@author huxueyang
 *@dare 2023/4/20 9:24
 */
@Controller
@RequestMapping("/zfbpay")
@ResponseBody
public class ZFBPayController {
    @Resource
    private ZFBPayUtil zfbPayUtil;
    // 支付宝下单
    @PostMapping("/zfbPay")
    public Map<String, Object> pay(@RequestBody Map<String,Object> map) {
        return zfbPayUtil.zfbPay(map);
    }

    // 查询支付宝订单订单
    @PostMapping("/zfbPayQuery")
    public Map<String, Object> queryMyOrderId(@RequestBody Map<String,Object> map) {
        return zfbPayUtil.zfbPayQuery(map);
    }
    //    支付宝关单
    @PostMapping("/closeZfbOrder")
    public Map<String, Object> closeOrderId(@RequestBody Map<String,Object> map) {
       return zfbPayUtil.closeZfbOrder(map);
    }

    //    支付回调,退款回调等
    @PostMapping("/zfbNotify")
    public Map<String, Object> notify(@RequestParam Map<String, String> params) {
        return zfbPayUtil.zfbNotify(params);
    }
  //退款
    @PostMapping("/zfbRefund")
    public Map<String, Object> refund(@RequestBody Map<String,Object> map) {
        return zfbPayUtil.zfbRefund(map);
    }

    //支付宝退款查询
    @PostMapping("/zfbRefundQuery")
    public Map<String, Object> refundQuery(@RequestBody Map<String,Object> map) {
        return zfbPayUtil.zfbRefundQuery(map);
    }
}
