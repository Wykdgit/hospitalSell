package com.wizz.hospitalSell.controller;


import com.wizz.hospitalSell.config.ProjectConfig;
import com.wizz.hospitalSell.constant.CookieConstant;
import com.wizz.hospitalSell.constant.RedisConstant;
import com.wizz.hospitalSell.domain.UserInfo;
import com.wizz.hospitalSell.enums.ResultEnum;
import com.wizz.hospitalSell.service.AdminService;
import com.wizz.hospitalSell.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户有关
 * Created By Cx On 2018/7/30 9:45
 */
@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProjectConfig projectConfig;

    /**
     * 登录页面跳转路由
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index(@RequestParam(required = false) String error, HttpServletRequest request) {
        Map<String, Object> m = new HashMap<>();
        if (error != null) m.put("error",error);
        m.put("username",CookieUtil.getCookie(request,"username"));
        m.put("password",CookieUtil.getCookie(request,"password"));
        return new ModelAndView("common/index",m);
    }

    /**
     * 管理员登录路由
     */
    @PostMapping("/login")
    public ModelAndView login(String username, String password, HttpServletResponse response) {
        Map<String, Object> m = new HashMap<>();
        m.put("url", "/seller/order/list");
        //1.在数据库中查询该用户是否存在
        if (!adminService.isAdminExist(username,password)) {
            m.put("msg", ResultEnum.LOGIN_FAIL.getMsg());
            return new ModelAndView("common/error", m);
        }

        //2.设置token 到 redis
        //生成随机数，官方解释：使用加密的强伪随机数生成器生成该 UUID
        String token = UUID.randomUUID().toString();
        //过期时间，expire:期满
        Integer expire = RedisConstant.EXPIRE;
        //opsForValue表示对某个值进行操作，set参数：key、value、过期时间、时间单位
        //String.format(RedisConstant.TOKEN_PREFIX,token):将token按前者的格式，格式化
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), username, expire, TimeUnit.SECONDS);

        //3.设置token 到 cookie
        CookieUtil.setCookie(response, CookieConstant.TOKEN, token, CookieConstant.EXPIRE);
        return new ModelAndView("redirect:" + projectConfig.getSell() + "/seller/order/list");
    }

    /**
     * 管理员登出路由
     */
    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> m = new HashMap<>();
        //获取cookie
        Cookie cookie = CookieUtil.getCookie(request, CookieConstant.TOKEN);
        if (cookie != null) {
            //若cookie存在，清除redis和cookie
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));
            CookieUtil.setCookie(response, CookieConstant.TOKEN, null, 0);
        }
        m.put("msg", ResultEnum.LOGOUT_SUCCESS.getMsg());
        m.put("url", "/seller/order/list");
        return new ModelAndView("common/success", m);
    }
}