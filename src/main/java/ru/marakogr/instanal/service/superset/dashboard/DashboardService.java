package ru.marakogr.instanal.service.superset.dashboard;

import java.util.List;
import ru.marakogr.instanal.db.model.Dashboard;
import ru.marakogr.instanal.db.model.FriendRelation;

public interface DashboardService {

  List<Dashboard> findByRelation(FriendRelation relation);

  Dashboard createDashboard(FriendRelation relation, String name, List<String> chartIds);

  String generateGuestLink(FriendRelation friendRelation, Dashboard dashboard);
}
