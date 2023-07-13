package com.wxpaydemo.util;/**
 * @author husong
 * @create 2023-04-19 15:07
 */

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

/**
 *@author huxueyang
 *@dare 2023/4/19 15:07
 */
public class CommonUtils {

    //微信支付回调通知获取参数
    public static String getBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        InputStream inputStream = request.getInputStream();
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                requestBody.append(charBuffer, 0, bytesRead);
            }
        }
        String requestBodyStr = requestBody.toString();
        return requestBodyStr;
    }
    public static String  copyMap(Map<String,Object> source,Map<String,Object> target){
        if (source==null||target==null){
            return "参数错误";
        }
        Set<String> strings = source.keySet();
        for (String string : strings) {
            if (target.get("string")==null||target.get("string")==""){
                target.put(string,source.get(string));
            }
        }
        return "成功";
    }
    //都不为空为空返回true
    public static boolean  validateNoNull(Object ...data){
        for (Object datum : data) {
            if (datum==null||datum==""){
                return false;
            }
        }
        return true;
    }
    //有一个不为空为空返回true
    public static boolean  validateOne(Object ...data){
        for (Object datum : data) {
            if (!(datum==null||datum=="")){
                return true;
            }
        }
        return false;
    }
}
