package ru.marakogr.instanal.service.superset.chart;

import java.util.List;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;

public interface ChartService {

  List<ChartInfo> get(FriendRelation relation, List<String> chartIds);

  List<String> getPossibleCharts();
}
