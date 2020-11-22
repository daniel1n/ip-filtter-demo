package com.qqlin.ipFilterDemo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lin.qingquan
 * @version 1.0
 * @date 2020-11-22 17:29
 * @Description: 自定义过滤器，用来判断 IP访问次数是否超限。
 * <p>
 * 如果前台用户访问网站的评率过快（达到超过50次/秒），则判定该ip恶意刷新操作
 * 限制该IP的访问，5分钟后自己解除限制
 * </P>
 */
@WebFilter(urlPatterns = "/*")
public class IpFilter implements Filter {

    /**
     * slf4j.Logger 日志变量
     */
    private final Logger log = LoggerFactory.getLogger(IpFilter.class);

    /**
     * 默认限制时间（单位：ms）
     */
    private static final long LIMITED_TIME_MILLIS = 5 * 60 * 1000;

    /**
     * 用户连续访问最高阈值：超过该值则认定为恶意操作的IP，进行限制
     */
    private static final int LIMIT_NUMBER = 50;

    /**
     * 用户访问最小安全时间：在该时间内如果访问吃书大于阈值，则记录为恶意IP，否则视为正常访问
     */
    private static final int MIN_SAFE_TIME = 5000;

    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 设置属性filterConfig
        this.config = filterConfig;
    }

    /**
     * 过滤器的设置
     *
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ServletContext context = config.getServletContext();

        // 获取限制IP存储器：存储被限制的IP信息
        HashMap<String, Long> limitedIpMap = (HashMap<String, Long>) context.getAttribute("limitedIpMap");

        // 过滤受限的IP
        filterLimitedIpMap(limitedIpMap);

        // 获取用户IP
        String ip = request.getRemoteAddr();
        log.info("获取用户IP: " + ip);

        // 判断是否被限制的IP，如果是则跳到异常页面
        if (isLimitedIP(limitedIpMap, ip)) {
            long limitedTime = limitedIpMap.get(ip) - System.currentTimeMillis();
            // 剩余限制时间（用为从毫秒到秒转化的一定会存在些许误差，但基本可以忽略不计）
            request.setAttribute("remainingTime", ((limitedTime / 1000) + (limitedTime % 1000 > 0 ? 1 : 0)));
            log.error("IP访问过于频繁=>：" + ip);
            log.info("<b>由于您访问过于频繁，被系统自动认定为机器人。5分钟自动解除</b>");
            request.getRequestDispatcher("/error/requestLimit").forward(request, response);
            return;
        }
        // 获取IP存储器
        HashMap<String, Long[]> ipMap = (HashMap<String, Long[]>) context.getAttribute("ipMap");
        // 判断存储器中是否存在当前IP，如果没有则为初次访问，初始化该IP
        // 如果存在当前IP，则验证当前IP的访问次数
        // 如果大于限制阈值，判断达到阈值的时间，如果不大于[用户访问最小安全时间]则视为恶意访问，跳转到异常页面
        if (ipMap.containsKey(ip)) {
            Long[] ipInfo = ipMap.get(ip);
            ipInfo[0] = ipInfo[0] + 1;
            log.info("当前第[" + (ipInfo[0]) + "]次数访问");
            if (ipInfo[0] > LIMIT_NUMBER) {
                Long ipAccessTime = ipInfo[1];
                Long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - ipAccessTime <= MIN_SAFE_TIME) {
                    limitedIpMap.put(ip, currentTimeMillis + LIMITED_TIME_MILLIS);
                    request.setAttribute("remainingTime", LIMITED_TIME_MILLIS);
                    log.error("ip访问过于频繁: " + ip);
                    request.getRequestDispatcher("/error/requestLimit").forward(request, response);
                    return;
                } else {
                    initIpVisitsNumber(ipMap, ip);
                }
            }
        } else {
            initIpVisitsNumber(ipMap, ip);
            log.info("首次访问该网站");
        }
        context.setAttribute("ipMap", ipMap);
        filterChain.doFilter(request, response);
    }


    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * 过滤受限的IP，删除已经到期的限制IP
     *
     * @param limitedIpMap
     */
    private void filterLimitedIpMap(HashMap<String, Long> limitedIpMap) {
        if (limitedIpMap == null) {
            return;
        }
        Set<String> keys = limitedIpMap.keySet();
        Iterator<String> keyIterator = keys.iterator();
        long currentTimeMillis = System.currentTimeMillis();
        while (keyIterator.hasNext()) {
            long expireTimeMillis = limitedIpMap.get(keyIterator.next());
            if (expireTimeMillis <= currentTimeMillis) {
                keyIterator.remove();
            }
        }
    }

    /**
     * 是否是被限制的IP
     *
     * @param limitedIpMap
     * @param ip
     * @return true : 被限制 || false : 正常
     */
    private boolean isLimitedIP(HashMap<String, Long> limitedIpMap, String ip) {
        if (limitedIpMap == null || ip == null) {
            // 没有被限制
            return false;
        }
        Set<String> keys = limitedIpMap.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (key.equals(ip)) {
                // 被限制的IP
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化用户访问次数和访问时间
     *
     * @param ipMap
     * @param ip
     */
    private void initIpVisitsNumber(HashMap<String, Long[]> ipMap, String ip) {
        Long[] ipInfo = new Long[2];
        // 访问次数
        ipInfo[0] = 0L;
        // 初次访问时间
        ipInfo[1] = System.currentTimeMillis();
        ipMap.put(ip, ipInfo);
    }
}
