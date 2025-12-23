package ru.marakogr.instanal.service.superset.dataset;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

@Component
@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {
  private final List<DatasetProvider> datasetProviders;

  @Transactional
  @Override
  public DatasetInfo get(FriendRelation relation, String datasetName) {
    return datasetProviders.stream()
        .filter(datasetProvider -> datasetName.equals(datasetProvider.name()))
        .findFirst()
        .map(datasetProvider -> datasetProvider.provide(relation))
        .orElseThrow(
            () -> new RuntimeException("not found dataset template for required table name"));
  }
}
