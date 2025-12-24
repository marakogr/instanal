package ru.marakogr.instanal.integration.superset.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChartCreateResponse {
  private Integer id;
  private ChartLightResponse result;
}
