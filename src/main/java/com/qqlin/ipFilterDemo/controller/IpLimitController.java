package com.qqlin.ipFilterDemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lin.qingquan
 * @version 1.0
 * @date 2020-11-22 17:29
 * @Description: 对高频 ip 限制，携带剩余限制时间
 */
@RestController
public class IpLimitController {

    @RequestMapping("/error/requestLimit")
    public String requestLimitTime(HttpServletRequest request) {
        Object limitTime = request.getAttribute("remainingTime");
        return "IP已被限制，请稍后在试~" + limitTime;
    }
}
