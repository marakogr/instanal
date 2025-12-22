package ru.marakogr.instanal.integration.superset.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupersetAuthInterceptor implements RequestInterceptor {

  private final SupersetAuthService authService;

  @Override
  public void apply(RequestTemplate template) {
    template.header("Cookie", authService.getSessionCookie());
  }
}
