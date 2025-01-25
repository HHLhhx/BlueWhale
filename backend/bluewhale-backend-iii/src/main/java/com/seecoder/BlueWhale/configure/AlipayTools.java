package com.seecoder.BlueWhale.configure;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import lombok.Getter;
import lombok.Setter;

import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "alipay")
@Component
@Getter
@Setter
public class AlipayTools {

    private String serverUrl;

    private String returnUrl;

    private String appId;

    private String appPrivateKey;

    private String alipayPublicKey;

    private String notifyUrl;

    private static String format = "json";

    private static String charset = "utf-8";

    private static String signType = "RSA2";

    /**
     * @Author: DingXiaoyu
     * @Date: 11:25 2024/1/31
     *        使用支付宝沙箱
     *        使用时可以根据自己的需要做修改，包括参数名、返回值、具体实现
     *        在bizContent中放入关键的信息：tradeName、price、name
     *        返回的form是一个String类型的html页面
     */
    public String pay(JSONObject bizContent, Map<String, String> notifyParams, Map<String, String> returnParams) {
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, appPrivateKey, format, charset,
                alipayPublicKey, signType);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        boolean firstTime = true;
        StringBuilder notifyUrlStringBuilder = new StringBuilder(notifyUrl + "?");
        for (String key : notifyParams.keySet()) {
            if (firstTime) {
                firstTime = false;
            } else {
                notifyUrlStringBuilder.append("&");
            }
            notifyUrlStringBuilder.append(key);
            notifyUrlStringBuilder.append("=");
            notifyUrlStringBuilder.append(notifyParams.get(key));
        }
        String gotNotifyUrl = notifyUrlStringBuilder.toString();
        System.out.println(gotNotifyUrl);
        request.setNotifyUrl(gotNotifyUrl);

        firstTime = true;
        StringBuilder returnUrlStringBuilder = new StringBuilder(returnUrl + "?");
        for (String key : returnParams.keySet()) {
            if (firstTime) {
                firstTime = false;
            } else {
                returnUrlStringBuilder.append("&");
            }
            returnUrlStringBuilder.append(key);
            returnUrlStringBuilder.append("=");
            returnUrlStringBuilder.append(returnParams.get(key));
        }
        request.setReturnUrl(returnUrlStringBuilder.toString());
        System.out.println(returnUrlStringBuilder);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String form;
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (Exception e) {
            throw BlueWhaleException.payError();
        }
        return form;
    }
}