package ru.marakogr.instanal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/superset/proxy")
public class SupersetProxyController {

  private final SupersetService supersetService;

  @GetMapping("/a/dashboard")
  public Object apiV1DashboardGet() {
    return supersetService.getApi(DashboardsApi.class).apiV1DashboardGet(null);
  }
}
