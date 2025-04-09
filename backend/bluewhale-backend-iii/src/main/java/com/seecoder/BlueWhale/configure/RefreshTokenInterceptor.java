package com.seecoder.BlueWhale.configure;

import cn.hutool.core.bean.BeanUtil;
import com.seecoder.BlueWhale.util.TokenUtil;
import com.seecoder.BlueWhale.util.UserHolder;
import com.seecoder.BlueWhale.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.seecoder.BlueWhale.util.RedisConstants.LOGIN_TOKEN_EXPIRE_TIME;
import static com.seecoder.BlueWhale.util.RedisConstants.LOGIN_TOKEN_KEY;

@Configuration
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty() || !tokenUtil.verifyToken(token)) {
            return true;
        }

        // 如果token合法，则从redis中获取用户信息
        String key = LOGIN_TOKEN_KEY + token;
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(key);
        // 用户不存在，即已过期
        if (userMap.isEmpty()) {
            return true;
        }

        // 将查询到的用户信息存入当前线程
        UserVO userVO = BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
        UserHolder.saveUser(userVO);

        // 刷新token的有效期
        redisTemplate.expire(key, LOGIN_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
