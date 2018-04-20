package com.exchange.web.mq.socket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.util.Map;

public class WSHandShakeHandler implements HandshakeHandler {

    @Override
    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Map<String, Object> attributes) throws HandshakeFailureException {
        if(request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            //客户端http请求中的 参数permessage-deflate来协商是否对传输的payload数据进行deflate压缩的
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        DefaultHandshakeHandler handler = new DefaultHandshakeHandler();
        return handler.doHandshake(request,response,wsHandler,attributes);
    }
}
