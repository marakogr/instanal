package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record DatasetPostRequest(
    String database,
    String schema,
    @JsonProperty("table_name") String tableName,
    String sql,
    @JsonIgnore long id,
    List<Long> owners)
    implements DatasetInfo {
  @Override
  public Long getId() {
    return id;
  }

  @Override
  @JsonIgnore
  public String getTableName() {
    return tableName;
  }
}
