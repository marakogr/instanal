package ru.marakogr.instanal.service.superset.chart.impl;

import static ru.marakogr.instanal.integration.superset.model.FilterOperator.EQ;
import static ru.marakogr.instanal.integration.superset.model.GetListSchemaDsl.singleFilter;

import java.util.Collections;
import java.util.List;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.model.*;
import ru.marakogr.instanal.service.superset.chart.ChartProvider;
import ru.marakogr.instanal.service.superset.dashboard.DashboardContext;

public abstract class AbstractChartProvider implements ChartProvider {
  private final ChartApi chartApi;

  protected AbstractChartProvider(SupersetService supersetService) {
    this.chartApi = supersetService.getApi(ChartApi.class);
  }

  @Override
  public String getId() {
    return slice();
  }

  @Override
  public ChartInfo provide(DashboardContext context, DatasetInfo dataset) {
    var ownerId = context.ownerId();
    var friendId = context.friendId();
    var owners = context.owners();
    var sliceName = slice() + " for chat between " + ownerId + " and " + friendId;
    var filter = singleFilter("slice_name", EQ, sliceName, 0, 1);
    var chartRequest =
        getChartRequest(
            dataset.getId(),
            vizType(),
            params(context),
            description(),
            owners,
            ownerId,
            friendId,
            sliceName);
    var result = chartApi.apiV1ChartGetList(filter).getData().getResult();
    if (result != null && !result.isEmpty()) {
      var response = result.getFirst();
      return chartRequest.toBuilder()
          .id(response.getId())
          .dashboards(response.getDashboardIds())
          .build();
    }
    var response = chartApi.apiV1ChartPost(chartRequest);
    return chartRequest.toBuilder().id(response.getData().getId().longValue()).build();
  }

  protected abstract String description();

  protected abstract String params(DashboardContext relation);

  protected abstract String vizType();

  protected abstract String slice();

  protected ChartPostRequest getChartRequest(
      Long datasetId,
      String vizType,
      String params,
      String description,
      List<Long> owners,
      String ownerId,
      String friendId,
      String sliceName) {
    return ChartPostRequest.builder()
        .sliceName(sliceName)
        .vizType(vizType)
        .datasourceId(datasetId)
        .owners(owners)
        .params(params)
        .description(description + " for chat between " + ownerId + " and " + friendId)
        .datasourceType("table")
        .dashboards(Collections.emptyList())
        .build();
  }
}
