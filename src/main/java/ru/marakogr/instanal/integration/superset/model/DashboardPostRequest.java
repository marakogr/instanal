package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record DashboardPostRequest(
    @JsonProperty("dashboard_title") String dashboardTitle,
    @JsonProperty("slug") String slug,
    @JsonProperty("json_metadata") String jsonMetadata,
    @JsonProperty("position_json") String positionJson,
    @JsonProperty("owners") List<Long> owners,
    @JsonProperty("published") Boolean published,
    @JsonProperty("css") String css) {}
