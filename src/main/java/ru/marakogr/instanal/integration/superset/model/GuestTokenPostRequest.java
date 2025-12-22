package ru.marakogr.instanal.integration.superset.model;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record GuestTokenPostRequest(
    SupersetUser user, List<Object> rls, List<SupersetResource> resources) {}
