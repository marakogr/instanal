package ru.marakogr.instanal.service.superset.dataset;

import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

public interface DatasetProvider {
  DatasetInfo provide(FriendRelation relation);

  DatasetService.DatasetType type();

  String name();
}
