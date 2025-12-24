package ru.marakogr.instanal.service.superset.dataset;

import static ru.marakogr.instanal.integration.superset.model.FilterOperator.EQ;
import static ru.marakogr.instanal.integration.superset.model.GetListSchemaDsl.singleFilter;

import java.util.List;
import java.util.Optional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DatabaseApi;
import ru.marakogr.instanal.integration.superset.api.DatasetApi;
import ru.marakogr.instanal.integration.superset.model.*;
import ru.marakogr.instanal.utils.Utils;

public abstract class AbstractDatasetProvider implements DatasetProvider {
  private final DatabaseApi databaseApi;
  private final DatasetApi datasetApi;

  protected AbstractDatasetProvider(SupersetService supersetService) {
    this.databaseApi = supersetService.getApi(DatabaseApi.class);
    this.datasetApi = supersetService.getApi(DatasetApi.class);
  }

  @Override
  public DatasetInfo provide(FriendRelation relation) {
    var ownerId = relation.getOwner().getInstagramId();
    var friendId = relation.getFriendSuperUser().getInstagramId();
    var chatId = Utils.getChatId(ownerId, friendId);
    var owners =
        List.of(
            1L,
            relation.getOwner().getSupersetUserId(),
            relation.getFriendSuperUser().getSupersetUserId());
    return getOrCreate(chatId, ownerId, friendId, owners);
  }

  protected DatasetPostRequest getOrCreate(
      String chatId, String ownerInstagramId, String friendInstagramId, List<Long> owners) {
    var tableName = type().tableName(name(), chatId);
    var filter = singleFilter("table_name", EQ, tableName, 0, 1);
    var result = datasetApi.apiV1DatasetGetList(filter).getData().getResult();
    if (result != null && !result.isEmpty()) {
      return DatasetPostRequest.builder()
          .id(Long.parseLong(result.getFirst().getId()))
          .tableName(tableName)
          .build();
    }
    var sql = sql(chatId, ownerInstagramId, friendInstagramId);
    var datasetPostRequest =
        DatasetPostRequest.builder()
            .database(databaseApi.apiV1DatabaseGet(null).getData().getResult().getFirst().getId())
            .schema("public")
            .tableName(tableName)
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
    return datasetPostRequest.toBuilder().id(datasetId).build();
  }

  protected abstract String sql(String chatId, String ownerId, String friendId);
}
