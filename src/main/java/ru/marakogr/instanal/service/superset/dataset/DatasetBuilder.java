package ru.marakogr.instanal.service.superset.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

@Component
@RequiredArgsConstructor
public class DatasetBuilder {
  private final List<DatasetGenerator> datasetGenerators;

  public List<DatasetInfo> build(FriendRelation relation, Map<String, List<String>> supersetToId) {
    var datasetInfos = datasetGenerators.stream().map(d -> d.generate(relation)).toList();
    datasetInfos.forEach(
        dataset ->
            supersetToId
                .computeIfAbsent("dataset", k -> new ArrayList<>())
                .add(dataset.getId().toString()));
    return datasetInfos;
  }
}
