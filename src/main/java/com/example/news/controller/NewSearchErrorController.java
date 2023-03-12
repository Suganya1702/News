package com.example.news.controller;

import com.example.news.model.ApplicationErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class NewSearchErrorController implements ErrorController {

    @RequestMapping("/error")
    public ApplicationErrorResponse handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ApplicationErrorResponse.build(status.toString(), "The request URL is not found in the server", "Not found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return ApplicationErrorResponse.build(status.toString(), "Unexpected Error", "Internal server error");

            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return ApplicationErrorResponse.build(status.toString(), "The request hostname is invalid or request Header Or Cookie Too Large or invalid URL", "Bad Request");
            }
        }
        return ApplicationErrorResponse.build("status code is null", "No status code found to determine the cause", "");
    }
}