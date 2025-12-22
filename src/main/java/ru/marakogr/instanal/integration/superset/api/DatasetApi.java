package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import javax.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.*;

public interface DatasetApi extends ApiClient.Api {

  @RequestLine("POST /api/v1/dataset/")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1DatasetPost(@RequestBody DatasetPostRequest request);

  @RequestLine("GET /api/v1/dataset/?q={q}")
  @Headers({
    "Accept: application/json",
  })
  ApiResponse<IdArrayWrapper> apiV1DatasetGetList(@Param("q") @Nullable GetListSchema q);

  @RequestLine("DELETE /api/v1/dataset/{id}")
  @Headers({
    "Accept: application/json",
  })
  ApiResponse<Void> apiV1DatasetDelete(@Param("id") String id);
}
