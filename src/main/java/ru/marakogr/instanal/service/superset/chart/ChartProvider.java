package ru.marakogr.instanal.service.superset.chart;

import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;
import ru.marakogr.instanal.service.superset.dashboard.DashboardContext;

public interface ChartProvider {
  ChartInfo provide(DashboardContext relation, DatasetInfo dataset);

  default int order() {
    return 0;
  }

  String getId();

  String datasetName();
}
