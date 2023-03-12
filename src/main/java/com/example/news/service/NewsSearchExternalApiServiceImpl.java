package com.example.news.service;

import com.example.news.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsSearchExternalApiServiceImpl implements NewsSearchService {

    public static final String NEWS_SEARCH_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    @Autowired
    ResponseBuilder responseBuilder;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Environment environment;

    @Override
    @Cacheable(value = "news-search-cache", key = "'newsSearchResponseCache'+#newsSearchRequest.searchKeyword+#newsSearchRequest.publishedAt", condition = "#newsSearchRequest.searchKeyword!=null")
    public NewsGroup getNewsSearchResponse(NewsSearchExternalApiRequest newsSearchRequest) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String publishedFrom, publishedTo = "";
        Optional<String> publishedAt = Optional.ofNullable(newsSearchRequest.getPublishedAt());
        if (!publishedAt.isPresent()) {
            LocalDateTime tweleveHoursBefore = currentDateTime.minusHours(12);
            publishedFrom = tweleveHoursBefore.format(DateTimeFormatter.ofPattern((NEWS_SEARCH_API_DATE_FORMAT)));
        } else {
            publishedFrom = newsSearchRequest.getPublishedAt();
        }
        publishedTo = currentDateTime.format(DateTimeFormatter.ofPattern(NEWS_SEARCH_API_DATE_FORMAT));

        ResponseEntity<NewsSearchExternalApiResponse> responseEntity = restTemplate.getForEntity(prepareUrlWithQueryParams(newsSearchRequest.getSearchKeyword(), publishedTo, publishedFrom), NewsSearchExternalApiResponse.class);
        NewsSearchExternalApiResponse newsSearchAPIResponse = responseEntity.getBody();
        List<NewsGroupEntries> newsGroupEnteries = null;
        if (newsSearchAPIResponse != null) {
            newsGroupEnteries = processNewSearchApiResponse(responseEntity.getBody());
            return NewsGroup.build(publishedFrom + " to " + publishedTo, newsGroupEnteries, newsSearchAPIResponse.getStatus(), newsSearchAPIResponse.getCode(), newsSearchAPIResponse.getMessage());
        }
        return NewsGroup.build(publishedFrom + " to " + publishedTo, newsGroupEnteries, newsSearchAPIResponse.getStatus(), newsSearchAPIResponse.getCode(), newsSearchAPIResponse.getMessage());

    }

    @Override
    public String getMode() {
        return "Online";
    }

    private String prepareUrlWithQueryParams(String searchKeyword, String publishedTo, String publishedFrom) {
        String apiKey = environment.getProperty("news.api.key");
        //String apiKey = environment.getProperty("news.api.wrong.key");
        String url = environment.getProperty("news.api.everything.url");
        String urlWithQueryParams = "";
        if (url != null && apiKey != null) {
            urlWithQueryParams = url.replace("{searchKeyword}", searchKeyword).replace("{publishedFrom}", publishedFrom)
                    .replace("{publishedTo}", publishedTo)
                    .replace("{apiKey}", apiKey);
        }
        return urlWithQueryParams;
    }

    private List<NewsGroupEntries> processNewSearchApiResponse(NewsSearchExternalApiResponse newsSearchResponse) {
        List<Articles> articles = newsSearchResponse.getArticles();
        List<NewsGroupEntries> newsGroupEnteriesList = new ArrayList<>();
        if (articles != null && articles.size() > 0) {
            newsGroupEnteriesList.add(groupEntriesByYear(articles));
            newsGroupEnteriesList.add(groupEntriesByDay(articles));
            newsGroupEnteriesList.add(groupEntriesByMonth(articles));
            newsGroupEnteriesList.add(groupEntriesByWeeks(articles));
        }
        return newsGroupEnteriesList;
    }

    private NewsGroupEntries groupEntriesByDay(List<Articles> articles) {

        Map<Object, List<Articles>> groupByday = articles.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.parse(o.getPublishedAt(), DateTimeFormatter.ofPattern(NEWS_SEARCH_API_DATE_FORMAT)).with(TemporalAdjusters.ofDateAdjuster(d -> d)).getDayOfMonth()));
        return processGroupByData(groupByday, "Day");

    }

    private NewsGroupEntries processGroupByData(Map<Object, List<Articles>> groupByday, String groupedBy) {
        NewsGroupEntries newsGroupEnteries = new NewsGroupEntries();
        List<NewsGroupEntry> newsGroupEntryList = new ArrayList<>();
        groupByday.entrySet().stream().forEach(day -> {
            NewsGroupEntry newsGroupEntry = new NewsGroupEntry();
            newsGroupEntry.setGroupEntries(day.getValue());
            newsGroupEntry.setGroupSize(day.getValue().size());
            newsGroupEntry.setGroupName(day.getKey());
            newsGroupEntryList.add(newsGroupEntry);
        });
        return NewsGroupEntries.build(newsGroupEntryList, groupedBy);
    }

    private NewsGroupEntries groupEntriesByWeeks(List<Articles> articles) {

        Map<Object, List<Articles>> groupByWeeks = articles.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.parse(o.getPublishedAt(), DateTimeFormatter.ofPattern(NEWS_SEARCH_API_DATE_FORMAT)).with(TemporalAdjusters.previousOrSame(DayOfWeek.of(1))).getDayOfWeek()));
        return processGroupByData(groupByWeeks, "Week");
    }

    private NewsGroupEntries groupEntriesByMonth(List<Articles> articles) {

        Map<Object, List<Articles>> groupByMonth = articles.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.parse(o.getPublishedAt(), DateTimeFormatter.ofPattern(NEWS_SEARCH_API_DATE_FORMAT)).with(TemporalAdjusters.firstDayOfMonth()).getMonth()));
        return processGroupByData(groupByMonth, "Month");

    }

    private NewsGroupEntries groupEntriesByYear(List<Articles> articles) {
        Map<Object, List<Articles>> groupByYear = articles.stream()
                .collect(Collectors.groupingBy(o ->
                        LocalDateTime.parse(o.getPublishedAt(), DateTimeFormatter.ofPattern(NEWS_SEARCH_API_DATE_FORMAT)).with(TemporalAdjusters.firstDayOfYear()).getYear()));
        return processGroupByData(groupByYear, "Year");
    }

}
