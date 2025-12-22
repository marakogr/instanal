package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record ChartPostRequest(
    @JsonProperty("slice_name") String sliceName,
    @JsonProperty("viz_type") String vizType,
    @JsonProperty("datasource_id") Long datasourceId,
    @JsonProperty("owners") List<Long> owners,
    @JsonProperty("params") String params,
    @JsonProperty("description") String description,
    @JsonProperty("datasource_type") String datasourceType,
    @JsonIgnore Long id,
    List<Integer> dashboards)
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
}
