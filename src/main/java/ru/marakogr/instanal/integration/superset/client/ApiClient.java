package ru.marakogr.instanal.integration.superset.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Logger;
import feign.RequestInterceptor;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ApiClient {

  public interface Api {}

  protected final ObjectMapper objectMapper;
  protected final String basePath;
  protected Feign.Builder feignBuilder;

  public ApiClient(RequestInterceptor authorization, String basePath, ObjectMapper objectMapper) {
    this.basePath = basePath;
    this.objectMapper = objectMapper;
    this.feignBuilder =
        Feign.builder()
            .encoder(new FormEncoder(new JacksonEncoder(objectMapper)))
            .decoder(new ApiResponseDecoder(objectMapper))
            .requestInterceptor(authorization)
            .logger(new Slf4jLogger())
            .logLevel(Logger.Level.FULL);
  }

  public <T extends Api> T buildClient(Class<T> clientClass) {
    return feignBuilder.target(clientClass, basePath);
  }
}
