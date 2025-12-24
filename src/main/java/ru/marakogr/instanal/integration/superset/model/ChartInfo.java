package ru.marakogr.instanal.integration.superset.model;

import java.util.List;

public interface ChartInfo {
  Long getId();

  String getSlice();

  List<Integer> getDashboardIds();

  Long getDatasetId();
}
