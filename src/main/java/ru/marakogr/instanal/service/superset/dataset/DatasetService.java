package ru.marakogr.instanal.service.superset.dataset;

import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

public interface DatasetService {
  @Transactional
  DatasetInfo get(FriendRelation relation, String datasetName);

  enum DatasetType implements DatasetTableNameProvider {
    PUBLIC,
    PERSONAL() {
      @Override
      public String tableName(String datasetName, String chatId) {
        return datasetName + "_" + chatId;
      }
    }
  }

  interface DatasetTableNameProvider {
    default String tableName(String pattern, String chatId) {
      return pattern;
    }
  }
}
