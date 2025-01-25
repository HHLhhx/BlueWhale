package com.seecoder.BlueWhale.configure;

import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    SecurityUtil securityUtil;
    private static final Logger log = LoggerFactory.getLogger(AccessInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        User user = securityUtil.getCurrentUser();

        if (user == null || access == null) return true;

        //如果用户类型在权限数组中，说明权限足够
        RoleEnum type = user.getRole();

        if (Arrays.asList(access.roles()).contains(type)) {
            return true;
        } else {
            log.info("Illegal access to method {}", method.getName());
            throw BlueWhaleException.illegalUserAccess();
        }
    }
}
