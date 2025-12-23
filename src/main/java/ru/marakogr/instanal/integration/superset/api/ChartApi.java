package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import javax.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.*;

public interface ChartApi extends ApiClient.Api {

  @RequestLine("POST /api/v1/chart/")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1ChartPost(@RequestBody ChartPostRequest request);

  @RequestLine("PUT /api/v1/chart/{id}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1ChartPut(@Param("id") Long id, @RequestBody ChartPostRequest request);

  @RequestLine("DELETE /api/v1/chart/{id}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<Void> apiV1ChartDelete(@Param("id") String id);

  @RequestLine("GET /api/v1/chart/?q={q}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdArrayWrapper> apiV1ChartGetList(@Param("q") @Nullable GetListSchema filter);
}
