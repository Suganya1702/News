package com.example.news.service;

import com.example.news.model.NewsGroup;
import com.example.news.model.NewsSearchExternalApiRequest;

public interface NewsSearchService {

    String getMode();

    NewsGroup getNewsSearchResponse(NewsSearchExternalApiRequest newsSearchRequest);
}
