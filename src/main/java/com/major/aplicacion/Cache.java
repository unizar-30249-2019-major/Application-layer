package com.major.aplicacion;

import com.major.aplicacion.Session.SessionInfo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Cache {
    private static Map<String, SessionInfo> cache = new HashMap<>();
    private static Map<String, Date> frecuency = new HashMap<>();

    public static void addItem(SessionInfo sessionInfo) {
        if (cache.containsKey(sessionInfo.getToken())) {
            cache.replace(sessionInfo.getToken(), sessionInfo);
        } else {
            cache.put(sessionInfo.getToken(), sessionInfo);
        }
    }

    public static Optional getItem(String token) {
        return Optional.of(cache.get(token));
    }

    public static Boolean containsToken(String token) {
        return cache.containsKey(token);
    }

    @Scheduled(fixedRate = 10000)
    public void clearCache()  {

    }
}
