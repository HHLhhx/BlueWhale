package com.seecoder.BlueWhale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.service.AliPayable;
import com.seecoder.BlueWhale.service.OrderService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

@RestController
@RequestMapping("/api/ali")
public class AliPayController {

    @Autowired
    private OrderService orderService;

    private Map<String, AliPayable> notifyServiceList = new HashMap<>();

    @PostConstruct
    public void init() {
        notifyServiceList.put("orderService", orderService);
    }

    // 这个被阿里调用了
    @PostMapping("/notify")
    public String notify(@RequestParam(value = "service") String notifyService, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, httpServletRequest.getParameter(name));
            }
            for (String pair : params.get("body").split(";")) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    params.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
            AliPayable service = notifyServiceList.get(notifyService);
            if (service != null) {
                return (service.payNotify(params)) ? "success" : "failure";
            } else {
                return "failure";
            }
        }
        return "failure";
    }

    @GetMapping("returnUrl")
    public void returnUrl(@RequestParam(value = "url") String url, HttpServletResponse httpServletResponse) {
        // visit url
        String filePath = "src/main/java/com/seecoder/BlueWhale/template/returnUrl.html";
        String html = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            html = fileContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        html = String.format(html, url);
        try {
            httpServletResponse.getWriter().write(html);// 直接将完整的表单html输出到页面
            httpServletResponse.getWriter().flush();
            httpServletResponse.getWriter().close();
        } catch (IOException e) {
            throw BlueWhaleException.payError();
        }
    }

}
