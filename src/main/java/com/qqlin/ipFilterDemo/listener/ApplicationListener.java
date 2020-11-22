package com.qqlin.ipFilterDemo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;

/**
 * @author lin.qingquan
 * @version 1.0
 * @date 2020-11-22 17:29
 * @Description: 设置监听器
 */
@WebListener
public class ApplicationListener implements ServletContextListener {

    private Logger log = LoggerFactory.getLogger(ApplicationListener.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("liting: contextInitialized");
        log.info("MuleApiEsbApplicationListener初始化成功");
        ServletContext context = servletContextEvent.getServletContext();
        // IP存储器
        HashMap<String, Long[]> ipMap = new HashMap<>();
        context.setAttribute("ipMap", ipMap);
        // 限制IP存储器：存储被限制的IP信息
        HashMap<String, Long> limitedIpMap = new HashMap<>();
        context.setAttribute("limitedIpMap", limitedIpMap);
        log.info("ipMap: " + ipMap.toString() + ";LimitedIpMap:" + limitedIpMap.toString() + "初始化成功-----");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        // TODO Auto-generated method stub
    }
}
