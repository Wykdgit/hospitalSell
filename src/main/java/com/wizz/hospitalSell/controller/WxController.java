package com.wizz.hospitalSell.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import com.wizz.hospitalSell.VO.ResultVO;
import com.wizz.hospitalSell.VO.UserVO;
import com.wizz.hospitalSell.domain.UserInfo;
import com.wizz.hospitalSell.dto.WxInfo;
import com.wizz.hospitalSell.service.UserService;
import com.wizz.hospitalSell.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 微信有关
 * Created By Cx On 2018/8/3 12:59
 */
@RestController
@RequestMapping("/wechat")
@Slf4j
public class WxController {

    @Autowired
    private WxMaService wxService;
    @Autowired
    private UserService userService;

    /**
     * 如果是get请求，不用@RequestBody，post请求必须加该注解
     * 登陆接口
     */
    @PostMapping("/login")
    public ResultVO login(@RequestBody WxInfo wxInfo, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isBlank(wxInfo.getCode())) {
            return ResultUtil.error("empty code");
        }

        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(wxInfo.getCode());
            log.info(session.getSessionKey());
            log.info(session.getOpenid());
            //TODO 可以增加自己的逻辑，关联业务相关数据
            UserInfo userInfo = userService.findByOpenid(session.getOpenid());
            if (userInfo == null){
                //若用户第一次登录,获取并存储用户信息
                WxMaUserInfo wxUserInfo = wxService.getUserService().getUserInfo(session.getSessionKey(), wxInfo.getEncryptedData(), wxInfo.getIv());
                userInfo = userService.save(new UserInfo(wxUserInfo.getNickName(),wxUserInfo.getOpenId(),wxUserInfo.getAvatarUrl()));
            }
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userInfo,userVO);
            return ResultUtil.success(userVO);
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return ResultUtil.error(e.getMessage());
        }
    }
}
