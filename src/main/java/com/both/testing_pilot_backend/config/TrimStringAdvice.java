package com.both.testing_pilot_backend.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Type;
import java.util.Arrays;

@ControllerAdvice
public class TrimStringAdvice implements RequestBodyAdvice {

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                              MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    Arrays.stream(body.getClass().getDeclaredFields())
            .filter(field -> field.getType().equals(String.class))
            .forEach(field -> {
              field.setAccessible(true);
              try {
                String value = (String) field.get(body);
                if (value != null) {
                  field.set(body, value.trim());
                }
              } catch (IllegalAccessException ignored) {}
            });
    return body;
  }

  // Required overrides
  @Override public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                                   Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return inputMessage;
  }

  @Override public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                          Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}
