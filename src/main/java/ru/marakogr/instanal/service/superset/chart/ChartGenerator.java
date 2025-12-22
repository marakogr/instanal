package ru.marakogr.instanal.service.superset.chart;

import java.util.List;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

public interface ChartGenerator {
  ChartInfo generate(FriendRelation relation, List<DatasetInfo> datasets);

  default int order() {
    return 0;
  }
}
