package ru.marakogr.instanal.integration.superset.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record SupersetResource(String type, String id) {}
