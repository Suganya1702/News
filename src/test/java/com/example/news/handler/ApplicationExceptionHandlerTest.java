//package com.example.news.handler;
//
//import com.example.news.advice.ApplicationExceptionHandler;
//import com.example.news.service.NewsSearchOnlineServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import javax.validation.ConstraintViolationException;
//
//@ExtendWith(SpringExtension.class)
//public class ApplicationExceptionHandlerTest {
//    ApplicationExceptionHandler applicationExceptionHandler;
//    @Mock
//    NewsSearchOnlineServiceImpl newsSearchOnlineService;
//    @Test
//    public void testService_WhenThrowInvalidApiKey_ReturnMockResponse(){
//        Mockito.when(newsSearchOnlineService.getNewsSearchResponse(Mockito.any())).thenThrow(new ConstraintViolationException());
//        Mockito.verify(applicationExceptionHandler.handleInvalidApiKey(Mockito.any()),Mockito.atLeast(1));
//
//    }
//}
