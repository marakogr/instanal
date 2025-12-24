package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record ChartLightResponse(
    @JsonProperty("slice_name") String sliceName,
    @JsonProperty("datasource_id") Long datasourceId,
    Long id,
    List<IdWrapper> dashboards)
    implements ChartInfo {
  @Override
  public Long getId() {
    return id;
  }

  @Override
  @JsonIgnore
  public String getSlice() {
    return sliceName;
  }

  @Override
  public List<Integer> getDashboardIds() {
    return dashboards == null
        ? Collections.emptyList()
        : dashboards.stream().map(IdWrapper::getId).map(Integer::parseInt).toList();
  }

  @Override
  public Long getDatasetId() {
    return datasourceId;
  }
}
