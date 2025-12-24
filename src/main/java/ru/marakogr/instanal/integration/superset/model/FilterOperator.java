package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FilterOperator {
  EQ("eq"),
  REL("rel_m_m");

  private final String value;

  FilterOperator(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
