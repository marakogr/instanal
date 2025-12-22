package ru.marakogr.instanal.service.superset.chart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;

@Component
@RequiredArgsConstructor
public class ChartsBuilder {
  private final List<ChartGenerator> chartGenerators;

  public List<ChartInfo> build(
      FriendRelation relation, Map<String, List<String>> supersetToId, List<DatasetInfo> datasets) {
    var chartInfos =
        chartGenerators.stream()
            .sorted(Comparator.comparingInt(ChartGenerator::order))
            .map(d -> d.generate(relation, datasets))
            .toList();
    chartInfos.forEach(
        chart ->
            supersetToId
                .computeIfAbsent("chart", k -> new ArrayList<>())
                .add(chart.getId().toString()));
    return chartInfos;
  }
}
