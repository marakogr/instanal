package ru.marakogr.instanal.service.superset.dataset;

import java.util.List;
import java.util.Optional;
import ru.marakogr.instanal.Utils;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DatabaseApi;
import ru.marakogr.instanal.integration.superset.api.DatasetApi;
import ru.marakogr.instanal.integration.superset.model.ApiResponse;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetPostRequest;
import ru.marakogr.instanal.integration.superset.model.IdWrapper;

public abstract class AbstractDataset implements DatasetGenerator {
  private final DatabaseApi databaseApi;
  private final DatasetApi datasetApi;

  protected AbstractDataset(SupersetService supersetService) {
    this.databaseApi = supersetService.getApi(DatabaseApi.class);
    this.datasetApi = supersetService.getApi(DatasetApi.class);
  }

  @Override
  public DatasetInfo generate(FriendRelation relation) {
    var ownerId = relation.getOwner().getInstagramId();
    var friendId = relation.getFriendSuperUser().getInstagramId();
    var chatId = Utils.getChatId(ownerId, friendId);
    var owners =
        List.of(
            1L,
            relation.getOwner().getSupersetUserId(),
            relation.getFriendSuperUser().getSupersetUserId());
    return create(relation, chatId, tableName(), ownerId, friendId, owners);
  }

  protected DatasetPostRequest create(
      FriendRelation relation,
      String chatId,
      String tableName,
      String ownerInstagramId,
      String friendInstagramId,
      List<Long> owners) {
    var sql = sql(chatId, ownerInstagramId, friendInstagramId);
    var datasetPostRequest =
        DatasetPostRequest.builder()
            .database(databaseApi.apiV1DatabaseGet(null).getData().getResult().getFirst().getId())
            .schema("public")
            .tableName(tableName + "_" + chatId)
            .sql(sql)
            .owners(owners)
            .build();
    var response = datasetApi.apiV1DatasetPost(datasetPostRequest);
    var datasetId =
        Optional.ofNullable(response)
            .map(ApiResponse::getData)
            .map(IdWrapper::getId)
            .map(Long::parseLong)
            .orElseThrow(() -> new RuntimeException("unable to create dataset for chat"));
    relation.setMaxReelsByDayDatasetId(datasetId);
    return datasetPostRequest.toBuilder().id(datasetId).build();
  }

  protected abstract String tableName();

  protected abstract String sql(String chatId, String ownerId, String friendId);
}
