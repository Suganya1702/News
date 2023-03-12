package com.example.news.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.RequestDispatcher;
import javax.ws.rs.core.MediaType;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NewSearchErrorController.class)
@AutoConfigureMockMvc
public class NewSearchErrorControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testHandleError_whenStatusCodeIsNull() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, null);
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/error").accept(
                MediaType.APPLICATION_JSON);
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"code\":\"status code is null\",\"possibleCauses\":\"No status code found to determine the cause\",\"status\":\"\"}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }

    @Test
    public void testHandleError_whenPageNotFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/error").accept(
                MediaType.APPLICATION_JSON).requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value());
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"code\":\"404\",\"possibleCauses\":\"The request URL is not found in the server\",\"status\":\"Not found\"}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }

    @Test
    public void testHandleError_whenInternalServerIsThrown() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/error").accept(
                MediaType.APPLICATION_JSON).requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"code\":\"500\",\"possibleCauses\":\"Unexpected Error\",\"status\":\"Internal server error\"}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }

    @Test
    public void testHandleError_whenBadRequestIsFound() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/error").accept(
                MediaType.APPLICATION_JSON).requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        MvcResult actual = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"code\":\"400\",\"possibleCauses\":\"The request hostname is invalid or request Header Or Cookie Too Large or invalid URL\",\"status\":\"Bad Request\"}";
        Assert.assertEquals(expected, actual.getResponse().getContentAsString());
    }
}
