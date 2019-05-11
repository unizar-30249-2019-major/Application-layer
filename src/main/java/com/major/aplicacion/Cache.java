package com.major.aplicacion;

import com.major.aplicacion.Session.SessionInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Cache {
    private static Map<String, SessionInfo> cache = new HashMap<>();
    private static Map<String, Date> frecuency = new HashMap<>();

    public static void push(SessionInfo sessionInfo) {
        if (cache.containsKey(sessionInfo.getToken())) {
            cache.replace(sessionInfo.getToken(), sessionInfo);
        } else {
            cache.put(sessionInfo.getToken(), sessionInfo);
        }
    }

    public static void pop(String token) {
        cache.remove(token);
    }

    public static void popById(long id) {
        for(Map.Entry<String, SessionInfo> entry : cache.entrySet()) {
            String token = entry.getKey();
            SessionInfo value = entry.getValue();
            if(value.getId() == id) {
                cache.remove(token);
                break;
            }
        }
    }

    public static Optional getItem(String token) {
        if (cache.containsKey(token)) {
            return Optional.of(cache.get(token));
        }
        return Optional.empty();
    }

    public static Boolean containsToken(String token) {
        return cache.containsKey(token);
    }

    @Scheduled(fixedRate = 10000)
    public void clearCache() {

    }
}
