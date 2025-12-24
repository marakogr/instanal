package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record ChartResponse(
    @JsonProperty("slice_name") String sliceName,
    @JsonProperty("viz_type") String vizType,
    @JsonProperty("datasource_id") Long datasourceId,
    @JsonProperty("owners") List<IdWrapper> owners,
    @JsonProperty("params") String params,
    @JsonProperty("description") String description,
    @JsonProperty("datasource_type") String datasourceType,
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
  @JsonIgnore
  public Long getDatasetId() {
    return datasourceId;
  }
}
