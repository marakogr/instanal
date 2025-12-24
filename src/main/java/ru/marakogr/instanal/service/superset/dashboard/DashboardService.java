package ru.marakogr.instanal.service.superset.dashboard;

import java.util.List;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DashboardInfo;

public interface DashboardService {

  List<DashboardInfo> findByRelation(FriendRelation relation);

  DashboardInfo createDashboard(FriendRelation relation, String name, List<String> chartIds);

  String generateGuestLink(FriendRelation friendRelation, DashboardInfo dashboard);
}
