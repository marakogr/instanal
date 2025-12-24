package ru.marakogr.instanal.integration.superset.model;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record DashboardLightResponse(Integer id, String slug, String url, String dashboard_title)
    implements DashboardInfo {
  @Override
  public Integer getId() {
    return id();
  }

  @Override
  public String getSlug() {
    return slug();
  }

  @Override
  public String getTitle() {
    return dashboard_title();
  }

  @Override
  public List<Long> getOwners() {
    return List.of();
  }
}
