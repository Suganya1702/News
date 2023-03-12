package com.example.news.controller;

import com.example.news.model.*;
import com.example.news.service.NewsSearchExternalApiServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NewsSearchController.class)
@AutoConfigureMockMvc
public class NewsSearchControllerTest {

    @InjectMocks
    NewsSearchController newsSearchController;
    List<Articles> mockArticlesList;
    List<NewsGroupEntries> mockNewsGroupEntryList;

//   ; @MockBean
//    private CacheManager cacheManager
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NewsSearchExternalApiServiceImpl newsSearchService;

    @BeforeEach
    public void initializeMocks() {
        Source mockSource = Source.build("apple", "apple");
        Articles mockArticles = Articles.build(mockSource, "Kris Holt", "'Silo' teaser reveals Apple's latest post-apocalyptic drama", "Apple", "httsps:falke.com", "https://image", "2023-09-09 23:09:45", "Apple is lanching new AI tool");
        List<Articles> mockArticlesList = new ArrayList<>();
        mockArticlesList.add(mockArticles);

        List<NewsGroupEntry> newsGroupEntryList = new ArrayList<>();
        newsGroupEntryList.add(NewsGroupEntry.build(100, mockArticlesList, "2023"));
        NewsGroupEntries newsGroupEntriesList = new NewsGroupEntries();
        newsGroupEntriesList.setNewsGroupEntry(newsGroupEntryList);
        newsGroupEntriesList.setGroupedBy("Year");
        List<NewsGroupEntries> mockNewsGroupEntryList = new ArrayList<>();
        mockNewsGroupEntryList.add(newsGroupEntriesList);
    }

    @Test
    public void getNewsSearchResponse_WhenParametersMissing() throws Exception {
        newsSearchService = Mockito.mock(NewsSearchExternalApiServiceImpl.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/news/search").accept(
                MediaType.APPLICATION_JSON);
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"intervalGroup\":\"\",\"newsGroupEnteries\":null,\"status\":\"error\",\"code\":\"parametersMissing :\",\"message\":\"Required request parameter 'searchkeyword' for method parameter type String is not present\"}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }

    @Test
    public void getNewsSearchResponse_WhenServiceCallSuccess() throws Exception {
        NewsGroup mockNewsResponse = NewsGroup.build("2023-03-03", mockNewsGroupEntryList, "success", null, null);
        NewsSearchExternalApiRequest newsSearchExternalApiRequest = Mockito.mock(NewsSearchExternalApiRequest.class);
        Mockito.when(newsSearchService.getNewsSearchResponse(Mockito.any()))
                .thenReturn(mockNewsResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/news/search?publishedat=2023-03-03&searchkeyword=apple").accept(
                MediaType.APPLICATION_JSON);
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"intervalGroup\":\"2023-03-03\",\"newsGroupEnteries\":null,\"status\":\"success\",\"code\":null,\"message\":null}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }

//    @Test
//    public void testFallBackMethodNewSearchResponse_ReturnsCacheResponse(){
//        NewsSearchResponse mockResponse = NewsSearchResponse.build("success",10,mockArticlesList,null,null);
////        Mockito.when(cacheManager.getInstance())
////                .thenReturn(put("newsSearchResponseCache",mockResponse));
//        NewsSearchController newsSearchController = new NewsSearchController();
//        NewsSearchResponse actualResult = newsSearchController.getNewsSearchResponseFallBack("apple","2023-09-07");
//        Assert.assertEquals("",actualResult);
//    }


}

