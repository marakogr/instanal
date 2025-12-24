package ru.marakogr.instanal.service.superset.chart;

import java.util.List;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.service.superset.dashboard.DashboardContext;

public interface ChartService {

  List<String> getPossibleCharts();

  void addToDashboard(ChartInfo chartInfo, Integer dashboardId);

  void addToDashboard(List<ChartInfo> chartInfo, Integer dashboardId);

  List<ChartInfo> get(DashboardContext dashboardContext);
}
