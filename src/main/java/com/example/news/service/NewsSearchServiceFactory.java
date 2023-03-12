package com.example.news.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsSearchServiceFactory {

    private static final Map<String, NewsSearchService> newsSearchServiceMap = new HashMap<>();
    @Autowired
    private List<NewsSearchService> services;

    public static NewsSearchService getService(String mode) {
        NewsSearchService service = newsSearchServiceMap.get(mode.toLowerCase());
        if (service == null) throw new RuntimeException("Unknown service mode: " + mode);
        return service;
    }

    @PostConstruct
    public void initMyServiceCache() {
        for (NewsSearchService service : services) {
            newsSearchServiceMap.put(service.getMode().toLowerCase(), service);
        }
    }
}