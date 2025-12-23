package ru.marakogr.instanal.db.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.marakogr.instanal.db.model.Dashboard;
import ru.marakogr.instanal.db.model.FriendRelation;

public interface DashboardRepository extends JpaRepository<Dashboard, String> {
  List<Dashboard> findByRelation(FriendRelation relation);
}
