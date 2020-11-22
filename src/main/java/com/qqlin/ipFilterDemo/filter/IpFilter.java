package com.qqlin.ipFilterDemo.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * 用户访问最小安全时间：在该时间内如果访问吃书大于阈值，则记录为恶意IP，否则视为正常访问
     */
    private static final int MIN_SAFE_TIME = 5000;

    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //设置属性filterConfig
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

    }

    @Override
    public void destroy() {

    }
}
