package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * springMVC中使用的拦截器
 *
 *  必须实现 HandlerInterceptor 接口
 *
 *  检验客户端访问时，刷新redis中的token的有效期
 */
public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;
    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 进入controller之前做登录校验
     * 判断当前登录用户是否存在，不存在则拦截。   不存在说明还没注册/或者没通过相应信息登陆/登录未操纵的时间太长导致系统自动退出登录了（从redis中把登录信息删除了）(此时需要重新登陆)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、判断是否需要拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            //没有，需要拦截，设置状态吗
            response.setStatus(401);
            return false;
        }
        //有用户则放行
        return true;

    }

    /**
     * 执行完毕之后销毁用户信息，避免内存泄露
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除用户，避免内存泄露
        UserHolder.removeUser();
    }
}
