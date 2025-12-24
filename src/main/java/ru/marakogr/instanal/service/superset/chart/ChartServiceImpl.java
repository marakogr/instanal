package ru.marakogr.instanal.service.superset.chart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.ChartResponse;
import ru.marakogr.instanal.mapper.ChartMapper;
import ru.marakogr.instanal.service.superset.dataset.DatasetService;

@Component
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
  private final List<ChartProvider> chartProviders;
  private final DatasetService datasetService;
  private final ChartMapper chartMapper;
  private final SupersetService supersetService;

  @Override
  public List<ChartInfo> get(FriendRelation relation, List<String> chartIds) {
    var chartProviderByIds =
        chartProviders.stream()
            .filter(chartProvider -> chartIds.contains(chartProvider.getId()))
            .sorted(Comparator.comparingInt(ChartProvider::order))
            .toList();

    return chartProviderByIds.stream()
        .map(
            chartProvider -> {
              var dataset = datasetService.get(relation, chartProvider.datasetName());
              return chartProvider.provide(relation, dataset);
            })
        .toList();
  }

  @Override
  public List<String> getPossibleCharts() {
    return chartProviders.stream().map(ChartProvider::getId).toList();
  }

  @Override
  public void addToDashboard(ChartInfo chartInfo, Integer dashboardId) {
    if (chartInfo instanceof ChartResponse chartResponse) {
      var postRequest = chartMapper.map(chartResponse);
      var dashboards = postRequest.dashboards();
      var newDashboards =
          dashboards == null ? new ArrayList<Integer>() : new ArrayList<>(dashboards);
      newDashboards.add(dashboardId);
      postRequest = postRequest.toBuilder().dashboards(newDashboards).build();
      supersetService.getApi(ChartApi.class).apiV1ChartPut(postRequest.id(), postRequest);
    }
  }

  @Override
  public void addToDashboard(List<ChartInfo> charts, Integer dashboardId) {
    charts.forEach(chartInfo -> addToDashboard(chartInfo, dashboardId));
  }
}
