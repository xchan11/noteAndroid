package com.example.note.utils.cookie_tool;

import com.example.note.utils.cookie_tool.cache.CookieCache;
import com.example.note.utils.cookie_tool.cache.SetCookieCache;
import com.example.note.utils.cookie_tool.persistence.CookiePersistor;
import com.example.note.utils.cookie_tool.persistence.SharedPrefsCookiePersistor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * CookieJar implementation with persistence.
 *
 * <p>Used for Spring Boot Session + Cookie auth (e.g. JSESSIONID).
 * It persists cookies across app restarts and automatically attaches them to requests.</p>
 */
public class AppCookieJar implements CookieJar {

    private final CookieCache cache;
    private final CookiePersistor persistor;

    public AppCookieJar(SetCookieCache cache, SharedPrefsCookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;
        this.cache.addAll(persistor.loadAll());
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cache.addAll(cookies);
        persistor.saveAll(cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> removedCookies = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();
            if (isCookieExpired(currentCookie)) {
                removedCookies.add(currentCookie);
                it.remove();
            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        if (!removedCookies.isEmpty()) {
            persistor.removeAll(removedCookies);
        }

        return validCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    public synchronized void clear() {
        cache.clear();
        persistor.clear();
    }
}