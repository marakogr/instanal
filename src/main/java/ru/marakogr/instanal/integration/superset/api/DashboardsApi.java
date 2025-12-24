package ru.marakogr.instanal.integration.superset.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.model.*;

public interface DashboardsApi extends ApiClient.Api {

  @RequestLine("GET /api/v1/dashboard/?q={q}")
  @Headers({
    "Accept: application/json",
  })
  ApiResponse<GetListResponse<DashboardResponse>> apiV1DashboardGet(
      @Param("q") @javax.annotation.Nullable GetListSchema q);

  @RequestLine("POST /api/v1/dashboard/")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<DashboardCreateResponse> apiV1DashboardPost(
      @RequestBody DashboardPostRequest request);

  @RequestLine("DELETE /api/v1/dashboard/{id}")
  @Headers({
    "Content-Type: application/json",
  })
  ApiResponse<Void> apiV1DashboardDelete(@Param("id") String id);
}
