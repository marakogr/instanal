package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.ApiResponse;
import ru.marakogr.instanal.integration.superset.model.ChartPostRequest;
import ru.marakogr.instanal.integration.superset.model.IdWrapper;

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
}
