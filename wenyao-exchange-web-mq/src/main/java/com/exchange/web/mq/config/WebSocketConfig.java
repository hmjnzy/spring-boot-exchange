package com.exchange.web.mq.config;

import com.exchange.web.mq.socket.WSHandShakeHandler;
import com.exchange.web.mq.socket.session.STOMPConnectEventListener;
import com.exchange.web.mq.socket.session.SocketSessionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * 配置WebSocket
 */
@Configuration//定义配置类，可替换xml配置文件
@EnableWebSocketMessageBroker//这时候控制器（controller）开始支持@MessageMapping,就像是使用@requestMapping一样。
                            //开启使用STOMP协议来传输基于代理（Message Broker）的消息
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/endpointAric")//注册一个STOMP的endpoint,并指定使用SockJS协议
                .setAllowedOrigins("*")//允许跨域
                .setHandshakeHandler(new WSHandShakeHandler())//配置客户端请求握手参数 如permessage-deflate
                .withSockJS();//使用SockJS协议

        registry.addEndpoint("/endpointChat")
                .setAllowedOrigins("*")//允许跨域
                .setHandshakeHandler(new WSHandShakeHandler())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {//配置消息代理(Message Broker)
        //广播式应配置一个 /topic消息代理, @SendTo发送给浏览器的前缀
        //registry.enableSimpleBroker("/topic");
        //点对点式应配置 /queue和 /topic消息代理 可以同时配多个
        registry.enableSimpleBroker("/simpleChat","/topic");
        //如配置 /app客户端订阅时把所有路径加上 /app/queue, /app/topic
        //registry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public SocketSessionRegistry SocketSessionRegistry() {//用户session记录类(Chat)
        return new SocketSessionRegistry();
    }

    @Bean
    public STOMPConnectEventListener STOMPConnectEventListener() {//STOMP监听类(Chat)
        return new STOMPConnectEventListener();
    }

}
