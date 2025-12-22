package ru.marakogr.instanal.service.superset.chart;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.model.*;

public abstract class AbstractChart implements ChartGenerator {
  private final ChartApi chartApi;

  protected AbstractChart(SupersetService supersetService) {
    this.chartApi = supersetService.getApi(ChartApi.class);
  }

  @Override
  public ChartInfo generate(FriendRelation relation, List<DatasetInfo> datasets) {
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var ownerId = owner.getInstagramId();
    var friendId = friend.getInstagramId();
    var owners = List.of(1L, owner.getSupersetUserId(), friend.getSupersetUserId());
    return createChart(
        datasetId(datasets, relation),
        slice(),
        vizTyp(),
        params(relation),
        description(),
        owners,
        ownerId,
        friendId);
  }

  protected abstract String description();

  protected abstract String params(FriendRelation relation);

  protected abstract String vizTyp();

  protected abstract String slice();

  protected abstract Long datasetId(List<DatasetInfo> datasets, FriendRelation relation);

  protected ChartPostRequest createChart(
      Long datasetId,
      String slice,
      String vizType,
      String params,
      String description,
      List<Long> owners,
      String ownerId,
      String friendId) {
    var chartRequest =
        ChartPostRequest.builder()
            .sliceName(slice + " for chat between " + ownerId + " and " + friendId)
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
