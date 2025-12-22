package ru.marakogr.instanal.service.superset.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.repository.FriendRelationRepository;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;
import ru.marakogr.instanal.integration.superset.api.DatasetApi;
import ru.marakogr.instanal.service.superset.chart.ChartsBuilder;
import ru.marakogr.instanal.service.superset.dataset.DatasetBuilder;

@Slf4j
@Service
public class DashboardService {
  private final FriendRelationRepository friendRelationRepository;
  private final DashboardsApi dashboardsApi;
  private final DatasetApi datasetApi;
  private final ChartApi chartApi;
  private final DatasetBuilder datasetBuilder;
  private final ChartsBuilder chartsBuilder;
  private final DashboardBuilder dashboardBuilder;

  public DashboardService(
      SupersetService supersetService,
      FriendRelationRepository friendRelationRepository,
      DatasetBuilder datasetBuilder,
      ChartsBuilder chartsBuilder,
      DashboardBuilder dashboardBuilder) {
    this.datasetBuilder = datasetBuilder;
    this.dashboardsApi = supersetService.getApi(DashboardsApi.class);
    this.datasetApi = supersetService.getApi(DatasetApi.class);
    this.chartApi = supersetService.getApi(ChartApi.class);
    this.friendRelationRepository = friendRelationRepository;
    this.chartsBuilder = chartsBuilder;
    this.dashboardBuilder = dashboardBuilder;
  }

  public void createPersonalDashboard(FriendRelation relation) {
    Map<String, List<String>> supersetToId = new HashMap<>();
    try {
      var datasets = datasetBuilder.build(relation, supersetToId);
      var charts = chartsBuilder.build(relation, supersetToId, datasets);
      var dashboardId = dashboardBuilder.build(relation, supersetToId, charts);

      relation.setDashboardSlug(dashboardId);
      friendRelationRepository.save(relation);
    } catch (Exception e) {
      try {
        log.error("error during dashboard creation: {}", e.getMessage());
        rollbackSuperset(supersetToId);
        throw e;
      } catch (Exception rollbackException) {
        log.warn("unable to rollback superset entities", rollbackException);
        throw rollbackException;
      }
    }
  }

  private void rollbackSuperset(Map<String, List<String>> supersetToId) {
    supersetToId.forEach(
        (type, ids) -> {
          switch (type) {
            case "dataset":
              {
                ids.forEach(datasetApi::apiV1DatasetDelete);
                break;
              }
            case "chart":
              {
                ids.forEach(chartApi::apiV1ChartDelete);
                break;
              }
            case "dashboard":
              {
                ids.forEach(dashboardsApi::apiV1DashboardDelete);
                break;
              }
          }
        });
  }
}
