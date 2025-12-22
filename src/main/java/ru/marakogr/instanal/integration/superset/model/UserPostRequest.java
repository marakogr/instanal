package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

@Builder
public record UserPostRequest(
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("username") String userName,
    String email,
    boolean active,
    List<Long> roles,
    String password) {}
