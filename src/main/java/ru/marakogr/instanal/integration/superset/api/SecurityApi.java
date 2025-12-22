package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.GuestTokenPostRequest;
import ru.marakogr.instanal.integration.superset.model.TokenWrapper;

public interface SecurityApi extends ApiClient.Api {

  @RequestLine("POST /api/v1/security/guest_token/")
  @Headers({
    "Content-Type: application/json",
  })
  TokenWrapper apiV1SecurityGuestTokenPost(@RequestBody GuestTokenPostRequest getGuestTokenRequest);
}
