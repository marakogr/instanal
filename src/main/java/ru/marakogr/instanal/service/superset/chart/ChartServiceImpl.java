package ru.marakogr.instanal.service.superset.chart;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.service.superset.dataset.DatasetService;

@Component
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {
  private final List<ChartProvider> chartProviders;
  private final DatasetService datasetService;

  @Override
  public List<ChartInfo> get(FriendRelation relation, List<String> chartIds) {
    var chartProviderByIds =
        chartProviders.stream()
            .filter(chartProvider -> chartIds.contains(chartProvider.getId()))
            .sorted(Comparator.comparingInt(ChartProvider::order))
            .toList();

    return chartProviderByIds.stream()
        .map(
            chartProvider -> {
              var dataset = datasetService.get(relation, chartProvider.datasetName());
              return chartProvider.provide(relation, dataset);
            })
        .toList();
  }

  @Override
  public List<String> getPossibleCharts() {
    return chartProviders.stream().map(ChartProvider::getId).toList();
  }
}
