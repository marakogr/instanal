package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
public record SupersetUser(
    @JsonProperty("username") String userName,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName) {}
