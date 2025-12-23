package ru.marakogr.instanal.service.superset.chart;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.model.*;

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
  public ChartInfo provide(FriendRelation relation, DatasetInfo dataset) {
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var ownerId = owner.getInstagram();
    var friendId = friend.getInstagram();
    var owners = List.of(1L, owner.getSupersetUserId(), friend.getSupersetUserId());
    return getOrCreate(
        dataset.getId(), vizTyp(), params(relation), description(), owners, ownerId, friendId);
  }

  protected abstract String description();

  protected abstract String params(FriendRelation relation);

  protected abstract String vizTyp();

  protected abstract String slice();

  protected ChartPostRequest getOrCreate(
      Long datasetId,
      String vizType,
      String params,
      String description,
      List<Long> owners,
      String ownerId,
      String friendId) {
    var sliceName = slice() + " for chat between " + ownerId + " and " + friendId;
    var filter = new GetListSchema();
    filter
        .addFiltersItem(
            new GetListSchemaFiltersInner().col("slice_name").opr("eq").value(sliceName))
        .pageSize(1)
        .page(0);
    var result = chartApi.apiV1ChartGetList(filter).getData().getResult();
    if (result != null && !result.isEmpty()) {
      return ChartPostRequest.builder()
          .id(Long.parseLong(result.getFirst().getId()))
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
    var chartRequest =
        ChartPostRequest.builder()
            .sliceName(sliceName)
            .vizType(vizType)
            .datasourceId(datasetId)
            .owners(owners)
            .params(params)
            .description(description + " for chat between " + ownerId + " and " + friendId)
            .datasourceType("table")
            .dashboards(Collections.emptyList())
            .build();
    var response = chartApi.apiV1ChartPost(chartRequest);
    var id =
        Optional.ofNullable(response)
            .map(ApiResponse::getData)
            .map(IdWrapper::getId)
            .map(Long::parseLong)
            .orElseThrow(() -> new RuntimeException("unable to create chart"));
    return chartRequest.toBuilder().id(id).build();
  }
}
