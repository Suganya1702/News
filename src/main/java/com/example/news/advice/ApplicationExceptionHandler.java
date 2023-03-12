package com.example.news.advice;

import com.example.news.model.NewsGroup;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;


@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public NewsGroup handleInvalidApiKey(ConstraintViolationException violationException) {
        return NewsGroup.build("", null, "error", "parametersMissing", violationException.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public NewsGroup handleInvalidArugument(MethodArgumentTypeMismatchException mismatchException) {
        return NewsGroup.build("", null, "error", "parametersMissing", "Required parameters are missing, the scope of your search is too broad. Please set any of the following required parameters and try again: searchKeyword");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public NewsGroup handleInvalidArugument(HttpClientErrorException httpClientErrorException) {
        if (httpClientErrorException.getStatusCode().value() == 401) {
            return NewsGroup.build("", null, "error", "apiKeyInvalid", "Your API key is invalid or incorrect. Check your key, or go to https://newsapi.org to create a free API key.");
        }
        return NewsGroup.build("", null, "error", httpClientErrorException.getStatusText(), httpClientErrorException.getMessage());

    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public NewsGroup handleInvalidArgument(MissingServletRequestParameterException servletRequestParameterException) {
        return NewsGroup.build("", null, "error", "parametersMissing :", servletRequestParameterException.getLocalizedMessage());
    }

}
