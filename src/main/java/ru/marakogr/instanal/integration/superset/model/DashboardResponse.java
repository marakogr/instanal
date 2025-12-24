package ru.marakogr.instanal.integration.superset.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record DashboardResponse(
    Integer id,
    String slug,
    String status,
    String url,
    String dashboard_title,
    List<IdWrapper> owners)
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
    return owners == null
        ? Collections.emptyList()
        : owners.stream().map(IdWrapper::getId).map(Long::parseLong).toList();
  }
}
