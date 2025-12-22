package ru.marakogr.instanal.integration.superset.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.marakogr.instanal.integration.superset.config.SupersetProperties;

@Service
@RequiredArgsConstructor
public class SupersetAuthService {

  private final SupersetProperties props;
  private final RestTemplate restTemplate = new RestTemplate();

  private String sessionCookie;

  public String getSessionCookie() {
    if (sessionCookie == null) {
      loginAndExtractCookie();
    }
    return sessionCookie;
  }

  private void loginAndExtractCookie() {
    String loginUrl = props.getUrl() + "/login/";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("username", props.getUsername());
    body.add("password", props.getPassword());
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
    String setCookieHeader = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    if (setCookieHeader != null) {
      this.sessionCookie = setCookieHeader.split(";")[0];
    } else {
      throw new RuntimeException("Failed to get session cookie from /login/");
    }
  }
}
