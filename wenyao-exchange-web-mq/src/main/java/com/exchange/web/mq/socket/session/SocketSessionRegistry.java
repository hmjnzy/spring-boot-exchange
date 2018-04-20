package com.exchange.web.mq.socket.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 用户Session记录类
 */
@Slf4j
public class SocketSessionRegistry {

    private final ConcurrentMap<String, Set<String>> userSessionIds = new ConcurrentHashMap();
    private final Object lock = new Object();

    public SocketSessionRegistry() {
    }

    /**
     *
     * Take SessionId
     */
    public Set<String> getSessionIds(String user) {
        Set set = this.userSessionIds.get(user);
        return set != null?set: Collections.emptySet();
    }

    /**
     * Take All Session
     */
    public ConcurrentMap<String, Set<String>> getAllSessionIds() {
        return this.userSessionIds;
    }

    /**
     * Register Session
     */
    public void registerSessionId(String userName, String sessionId) {

        log.info("Register Session: {}", userName);

        Assert.notNull(userName, "User must not be null");
        Assert.notNull(sessionId, "Session ID must not be null");
        synchronized(this.lock) {
            Set set = this.userSessionIds.get(userName);
            if (set == null) {
                set = new CopyOnWriteArraySet();
                this.userSessionIds.put(userName, (Set<String>) set);
            }

            set.add(sessionId);
        }
    }

    /**
     * Unset Session
     */
    public void unregisterSessionId(String userName, String sessionId) {

        log.info("Unset Session: {}", userName);

        Assert.notNull(userName, "User Name must not be null");
        Assert.notNull(sessionId, "Session ID must not be null");
        synchronized(this.lock) {
            Set set = this.userSessionIds.get(userName);
            if (set != null && set.remove(sessionId) && set.isEmpty()) {
                this.userSessionIds.remove(userName);
            }
        }
    }
}
