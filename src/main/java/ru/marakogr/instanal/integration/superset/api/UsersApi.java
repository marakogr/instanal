package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import javax.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.*;

public interface UsersApi extends ApiClient.Api {

  @RequestLine("POST /api/v1/security/users/")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1UserPost(@RequestBody UserPostRequest userPostRequest);

  @RequestLine("GET /api/v1/security/users/?q={q}")
  @Headers({
    "Accept: application/json",
  })
  ApiResponse<IdArrayWrapper> apiV1UsersGet(@Param("q") @Nullable GetListSchema q);
}
