package com.exchange.web.mq.socket.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

/**
 * STOMP监听类: 注册用户的 Session以及 Key值获取
 */
@Slf4j
public class STOMPConnectEventListener implements ApplicationListener<SessionConnectEvent> {

    @Autowired
    SocketSessionRegistry webAgentSessionRegistry;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {

        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        log.info("STOMP监听类 sha: {}", sha);

        //get from browser
        String agentId = sha.getNativeHeader("login").get(0);

        String sessionId = sha.getSessionId();
        webAgentSessionRegistry.registerSessionId(agentId, sessionId);
    }
}