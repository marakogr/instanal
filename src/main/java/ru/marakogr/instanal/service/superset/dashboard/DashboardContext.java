package ru.marakogr.instanal.service.superset.dashboard;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;

@Builder(toBuilder = true)
public record DashboardContext(
    FriendRelation relation,
    List<String> chartIds,
    String title,
    String ownerId,
    String friendId,
    List<Long> owners,
    List<ChartInfo> charts,
    LocalDate from,
    LocalDate to) {
  public boolean hasDateFilter() {
    return from != null || to != null;
  }

  public String supersetTimeRange() {
    if (from != null && to != null) {
      return from + " : " + to;
    }
    if (from != null) {
      return from + " : ";
    }
    if (to != null) {
      return " : " + to;
    }
    return "No filter";
  }
}
