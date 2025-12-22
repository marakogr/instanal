package ru.marakogr.instanal.integration.superset.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Logger;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.marakogr.instanal.integration.superset.auth.SupersetAuthInterceptor;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.client.RFC3339DateFormat;
import ru.marakogr.instanal.integration.superset.client.RFC3339JavaTimeModule;

@Configuration
public class ApacheSupersetConfig {

  @Bean
  public ApiClient supersetApiClient(
      SupersetProperties props, SupersetAuthInterceptor interceptor) {
    return new ApiClient(interceptor, props.getUrl(), createObjectMapper());
  }

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  protected ObjectMapper createObjectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.setDateFormat(new RFC3339DateFormat());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new RFC3339JavaTimeModule());
    JsonNullableModule jnm = new JsonNullableModule();
    objectMapper.registerModule(jnm);
    return objectMapper;
  }
}
