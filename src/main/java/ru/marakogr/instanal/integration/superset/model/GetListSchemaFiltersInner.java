package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Builder
public record GetListSchemaFiltersInner(
    @JsonProperty(value = "col", required = true) String col,
    @JsonProperty(value = "opr", required = true) String opr,
    @JsonProperty(value = "value", required = true) Object value) {}
