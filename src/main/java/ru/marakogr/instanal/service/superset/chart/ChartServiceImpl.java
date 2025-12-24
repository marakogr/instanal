package ru.marakogr.instanal.service.superset.chart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.ChartPostRequest;
import ru.marakogr.instanal.integration.superset.model.ChartResponse;
import ru.marakogr.instanal.mapper.ChartMapper;
import ru.marakogr.instanal.service.superset.dashboard.DashboardContext;
import ru.marakogr.instanal.service.superset.dataset.DatasetService;

@Component
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
  private final DatasetService datasetService;
  private final ChartMapper chartMapper;
  private final SupersetService supersetService;
  private final ChartProviderHolder chartProviderHolder;

  @Override
  public List<ChartInfo> get(DashboardContext context) {
    var chartIds = context.chartIds();
    var relation = context.relation();
    var chartProviderByIds =
        chartProviderHolder.getProviders().stream()
            .filter(chartProvider -> chartIds.contains(chartProvider.getId()))
            .sorted(Comparator.comparingInt(ChartProvider::order))
            .toList();

    return chartProviderByIds.stream()
        .map(
            chartProvider -> {
              var dataset = datasetService.get(relation, chartProvider.datasetName());
              return chartProvider.provide(context, dataset);
            })
        .toList();
  }

  @Override
  public List<String> getPossibleCharts() {
    return chartProviderHolder.getProviders().stream().map(ChartProvider::getId).toList();
  }

  @Override
  public void addToDashboard(ChartInfo chartInfo, Integer dashboardId) {
    ChartPostRequest postRequest;
    if (chartInfo instanceof ChartResponse chartResponse) {
      postRequest = chartMapper.map(chartResponse);
    } else if (chartInfo instanceof ChartPostRequest) {
      postRequest = (ChartPostRequest) chartInfo;
    } else throw new IllegalArgumentException("Unknown chart info type: " + chartInfo.getClass());
    var dashboards = postRequest.dashboards();
    var newDashboards = dashboards == null ? new ArrayList<Integer>() : new ArrayList<>(dashboards);
    newDashboards.add(dashboardId);
    postRequest = postRequest.toBuilder().dashboards(newDashboards).build();
    supersetService.getApi(ChartApi.class).apiV1ChartPut(postRequest.id(), postRequest);
  }

  @Override
  public void addToDashboard(List<ChartInfo> charts, Integer dashboardId) {
    charts.forEach(chartInfo -> addToDashboard(chartInfo, dashboardId));
  }
}
