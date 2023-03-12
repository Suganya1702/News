package com.example.news.controller;

import com.example.news.model.NewsGroup;
import com.example.news.model.NewsSearchExternalApiRequest;
import com.example.news.service.NewsSearchService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/news")
@Validated
public class NewsSearchController {

    @Autowired
    private NewsSearchService newsSearchService;


    @GetMapping(value = "/search")
    @HystrixCommand(fallbackMethod = "getNewsSearchResponseFallBack", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "120000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "50"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "100"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000")},
            ignoreExceptions = {ConstraintViolationException.class, MethodArgumentTypeMismatchException.class,
                    HttpClientErrorException.class, MissingServletRequestParameterException.class,
            }
    )
    public NewsGroup getNewsSearchResponse(@Valid @NotBlank @RequestParam(name = "searchkeyword") String searchKeyword,
                                           @RequestParam(name = "publishedat", required = false) String publishedAt) {
        NewsSearchExternalApiRequest newsSearchRequest = NewsSearchExternalApiRequest.build(searchKeyword, publishedAt);
        return newsSearchService.getNewsSearchResponse(newsSearchRequest);
    }

    public NewsGroup getNewsSearchResponseFallBack(@Valid @NotBlank @RequestParam(name = "searchkeyword") String searchKeyword,
                                                   @RequestParam(name = "publishedat") String publishedAt) {
        net.sf.ehcache.Cache cache = CacheManager.getInstance().getCache("news-search-cache");
        if (cache.get("newsSearchResponseCache" + searchKeyword + publishedAt) != null) {
            NewsGroup newsGroup = (NewsGroup) cache.get("newsSearchResponseCache" + searchKeyword + publishedAt).getObjectValue();
            if (newsGroup != null) {
                return newsGroup;
            }
        }
        return NewsGroup.build(publishedAt, null, "204", "No Content", "No Content available");

    }

}


