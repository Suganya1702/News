package com.example.news.service;

import com.example.news.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
public class NewsSearchOnlineServiceImplTest {
    @InjectMocks
    NewsSearchExternalApiServiceImpl newsSearchOnlineService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    Environment environment;


    List<Articles> mockArticlesList = new ArrayList<>();
    List<NewsGroupEntries> mockNewsGroupEntryList = new ArrayList<>();
    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        Source mockSource = Source.build("apple", "apple");
        Articles mockArticles = Articles.build(mockSource, "Kris Holt", "'Silo' teaser reveals Apple's latest post-apocalyptic drama", "Apple", "httsps:falke.com", "https://image", "2023-09-09T23:09:45Z", "Apple is lanching new AI tool");

        mockArticlesList.add(mockArticles);

        List<NewsGroupEntry> newsGroupEntryList = new ArrayList<>();
        newsGroupEntryList.add(NewsGroupEntry.build(1, mockArticlesList, 2023));
        NewsGroupEntries newsGroupEntriesList = new NewsGroupEntries();
        newsGroupEntriesList.setNewsGroupEntry(newsGroupEntryList);
        newsGroupEntriesList.setGroupedBy("Year");
        mockNewsGroupEntryList.add(newsGroupEntriesList);
    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsMockedObject() throws URISyntaxException, JsonProcessingException {
        NewsSearchExternalApiResponse newsSearchExternalApiResponse = NewsSearchExternalApiResponse.build("success", 100, mockArticlesList, "", "");
        NewsSearchExternalApiRequest request = NewsSearchExternalApiRequest.build("apple", null);
        Mockito.doReturn(new ResponseEntity(newsSearchExternalApiResponse, HttpStatus.OK)).when(restTemplate).getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.key")))
                .thenReturn("mockkey");
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.everything.url")))
                .thenReturn("https://mock-url/everything?q={searchKeyword}&from={publishedFrom}&to={publishedTo}&apiKey={apiKey}");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://mock-url/everything?q=apple&from=2022-02-02&to=2022-02-09&apiKey=mock-key")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON))
                        .body(mapper.writeValueAsString(newsSearchExternalApiResponse)));
        NewsGroup newsGroup = newsSearchOnlineService.getNewsSearchResponse(request);
        NewsGroup expectedNewsGroup = NewsGroup.build("2023-03-11T14:02:56Z to 2023-03-12T02:02:56Z", mockNewsGroupEntryList, "success", null, null);

        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().size(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().size());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupSize(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupSize());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupName(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupName());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupEntries().size(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupEntries().size());

    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsNullMockedObject() throws URISyntaxException, JsonProcessingException {
        NewsSearchExternalApiResponse newsSearchExternalApiResponse = NewsSearchExternalApiResponse.build("success", 0, new ArrayList<Articles>(), "", "");
        NewsSearchExternalApiRequest request = NewsSearchExternalApiRequest.build("apple", null);
        Mockito.doReturn(new ResponseEntity(newsSearchExternalApiResponse, HttpStatus.OK)).when(restTemplate).getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.key")))
                .thenReturn("mockkey");
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.everything.url")))
                .thenReturn("https://mock-url/everything?q={searchKeyword}&from={publishedFrom}&to={publishedTo}&apiKey={apiKey}");
        //Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));

//        Mockito.when(LocalDateTime.parse(ArgumentMatchers.anyString(),ArgumentMatchers.any()))
//                .thenReturn(LocalDateTime.parse("2023-03-0320:5:06"));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://mock-url/everything?q=apple&from=2022-02-02&to=2022-02-09&apiKey=mock-key")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON))
                        .body(mapper.writeValueAsString(newsSearchExternalApiResponse)));
        NewsGroup newsGroup = newsSearchOnlineService.getNewsSearchResponse(request);
        NewsGroup expectedNewsGroup = NewsGroup.build("2023-03-11T14:02:56Z to 2023-03-12T02:02:56Z", mockNewsGroupEntryList, "success", null, null);

        Assert.assertTrue(newsGroup.getNewsGroupEnteries().size() == 0);

    }


    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalledWithPublishedDate_thenReturnsMockedObject() throws URISyntaxException, JsonProcessingException {
        NewsSearchExternalApiResponse newsSearchExternalApiResponse = NewsSearchExternalApiResponse.build("success", 100, mockArticlesList, "", "");
        NewsSearchExternalApiRequest request = NewsSearchExternalApiRequest.build("apple", "2023-09-09T23:09:45Z");
        Mockito.doReturn(new ResponseEntity(newsSearchExternalApiResponse, HttpStatus.OK)).when(restTemplate).getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.key")))
                .thenReturn("mockkey");
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.everything.url")))
                .thenReturn("https://mock-url/everything?q={searchKeyword}&from={publishedFrom}&to={publishedTo}&apiKey={apiKey}");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://mock-url/everything?q=apple&from=2022-02-02&to=2022-02-09&apiKey=mock-key")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON))
                        .body(mapper.writeValueAsString(newsSearchExternalApiResponse)));
        NewsGroup newsGroup = newsSearchOnlineService.getNewsSearchResponse(request);
        NewsGroup expectedNewsGroup = NewsGroup.build("2023-03-11T14:02:56Z to 2023-03-12T02:02:56Z", mockNewsGroupEntryList, "success", null, null);

        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().size(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().size());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupSize(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupSize());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupName(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupName());
        Assert.assertEquals(expectedNewsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupEntries().size(), newsGroup.getNewsGroupEnteries().get(0).getNewsGroupEntry().get(0).getGroupEntries().size());

    }

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_thenReturnsNullNewsGroupMockedObject() throws URISyntaxException, JsonProcessingException {
        NewsSearchExternalApiResponse newsSearchExternalApiResponse = NewsSearchExternalApiResponse.build("error", 0, null, "mockcode", "mockMessage");
        NewsSearchExternalApiRequest request = NewsSearchExternalApiRequest.build("apple", "2023-09-09T23:09:45Z");
        Mockito.doReturn(new ResponseEntity(newsSearchExternalApiResponse, HttpStatus.OK)).when(restTemplate).getForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.key")))
                .thenReturn("mockkey");
        Mockito.when(environment.getProperty(ArgumentMatchers.eq("news.api.everything.url")))
                .thenReturn("https://mock-url/everything?q={searchKeyword}&from={publishedFrom}&to={publishedTo}&apiKey={apiKey}");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://mock-url/everything?q=apple&from=2022-02-02&to=2022-02-09&apiKey=mock-key")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON))
                        .body(mapper.writeValueAsString(newsSearchExternalApiResponse)));
        NewsGroup newsGroup = newsSearchOnlineService.getNewsSearchResponse(request);
        Optional<String> newGroupEntries = Optional.ofNullable(String.valueOf(newsGroup.getNewsGroupEnteries()));
        Assert.assertEquals("error", newsGroup.getStatus());
    }

}
