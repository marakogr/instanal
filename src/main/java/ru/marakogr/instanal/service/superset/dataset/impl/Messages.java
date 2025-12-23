package ru.marakogr.instanal.service.superset.dataset.impl;

import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetPostRequest;
import ru.marakogr.instanal.service.superset.dataset.DatasetProvider;
import ru.marakogr.instanal.service.superset.dataset.DatasetService;

@Component
public class Messages implements DatasetProvider {
  @Override
  public DatasetInfo provide(FriendRelation relation) {
    return DatasetPostRequest.builder().id(1L).tableName(name()).build();
  }

  @Override
  public DatasetService.DatasetType type() {
    return DatasetService.DatasetType.PUBLIC;
  }

  @Override
  public String name() {
    return "messages";
  }
}
