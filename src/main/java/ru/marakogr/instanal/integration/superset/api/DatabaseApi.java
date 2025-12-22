package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import javax.annotation.Nullable;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.ApiResponse;
import ru.marakogr.instanal.integration.superset.model.GetListSchema;
import ru.marakogr.instanal.integration.superset.model.IdArrayWrapper;

public interface DatabaseApi extends ApiClient.Api {

  @RequestLine("GET /api/v1/database/?q={q}")
  @Headers({
    "Accept: application/json",
  })
  ApiResponse<IdArrayWrapper> apiV1DatabaseGet(@Param("q") @Nullable GetListSchema q);
}
