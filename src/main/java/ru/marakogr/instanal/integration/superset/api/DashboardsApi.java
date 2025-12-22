package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.ApiResponse;
import ru.marakogr.instanal.integration.superset.model.DashboardPostRequest;
import ru.marakogr.instanal.integration.superset.model.GetListSchema;
import ru.marakogr.instanal.integration.superset.model.IdWrapper;

public interface DashboardsApi extends ApiClient.Api {

  @RequestLine("GET /api/v1/dashboard/?q={q}")
  @Headers({
    "Accept: application/json",
  })
  Object apiV1DashboardGet(@Param("q") @javax.annotation.Nullable GetListSchema q);

  @RequestLine("POST /api/v1/dashboard/")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1DashboardPost(@RequestBody DashboardPostRequest request);

  @RequestLine("PUT /api/v1/dashboard/{id}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<IdWrapper> apiV1DashboardPut(
      @Param("id") String id, @RequestBody DashboardPostRequest request);

  @RequestLine("DELETE /api/v1/dashboard/{id}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<Void> apiV1DashboardDelete(@Param("id") String id);
}
