package ru.marakogr.instanal.service.superset.chart;

import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

public interface ChartProvider {
  ChartInfo provide(FriendRelation relation, DatasetInfo dataset);

  default int order() {
    return 0;
  }

  String getId();

  String datasetName();
}
