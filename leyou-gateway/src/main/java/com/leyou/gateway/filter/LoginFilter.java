package com.leyou.gateway.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * <code>LoginFilter</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2020/01/03 23:11
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProp;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {

        //获取白名单
        List<String> allowPaths = this.filterProp.getAllowPaths();
        // 初始化运行上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        // 获取request对象
        HttpServletRequest request = requestContext.getRequest();
        // 获取请求路径
        String requestURI = request.getRequestURI().toString();
        // 判断白名单
        for (String allowPath : allowPaths) {
            if(StringUtils.contains(requestURI, allowPath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        // 初始化运行上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        // 获取request对象
        HttpServletRequest request = requestContext.getRequest();
        // 获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());

        /*if(StringUtils.isBlank(token)){
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }*/

        try {
            // 校验
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token, prop.getPublicKey());
        } catch (Exception e) {
            // 校验出现异常，返回403
            e.printStackTrace();
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(403);
        }
        return null;
    }
}