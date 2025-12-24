package ru.marakogr.instanal.integration.superset.model;

import java.util.List;

public interface DashboardInfo {
  Integer getId();

  String getSlug();

  String getTitle();

  List<Long> getOwners();
}
