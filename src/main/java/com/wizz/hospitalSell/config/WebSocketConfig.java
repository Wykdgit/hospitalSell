package com.wizz.hospitalSell.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * webSocket配置类
 * Created By Cx On 2018/8/1 13:26
 */
@Component
public class WebSocketConfig {

    //TODO 测试环境下要把webSocket的bean注释掉，因为测试时没有webSocket环境
    //@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
