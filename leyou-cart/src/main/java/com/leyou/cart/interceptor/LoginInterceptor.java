package com.leyou.cart.interceptor;

//springMVC的拦截器
//继承这个方法HandlerInterceptorAdapter 就不用重写HandlerInterceptor 方法  想用哪个方法就可以使用

import com.leyou.cart.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    // 使用线程域来存放
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1 获取cookie中的token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        //2 解析token ，获取用户信息
        UserInfo user = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        if(user == null){
            return false;
        }
        //3 把user存放在ThreadLocal中
        THREAD_LOCAL.set(user);
        return true;
    }
    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    /*
    * 释放线程对象 ，因为线程结束以后会回到线程池 ，线程永远都不可能消失 ，所以手动删除
    * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的局部变量，使用的是Tomcat的线程池，线程不会结束，不会释放线程的局部变量
        THREAD_LOCAL.remove();
    }
}
