package ru.marakogr.instanal.service.superset.chart.impl;

import static ru.marakogr.instanal.utils.Utils.getChatId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import org.apache.commons.text.StringSubstitutor;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.model.ChartPostRequest;
import ru.marakogr.instanal.service.superset.dashboard.DashboardContext;
import ru.marakogr.instanal.utils.ReflectUtils;

public class ConfigChartProvider extends AbstractChartProvider {
  private final ChartConfig chartConfig;

  public ConfigChartProvider(SupersetService supersetService, ChartConfig chartConfig) {
    super(supersetService);
    this.chartConfig = chartConfig;
  }

  @Override
  protected ChartPostRequest getChartRequest(
      Long datasetId,
      String vizType,
      String params,
      String description,
      List<Long> owners,
      String ownerId,
      String friendId,
      String sliceName) {
    return super.getChartRequest(
        datasetId, vizType, params, description, owners, ownerId, friendId, sliceName);
  }

  @Override
  protected String vizType() {
    return chartConfig.vizType();
  }

  @Override
  protected String slice() {
    return chartConfig.slice();
  }

  @Override
  protected String description() {
    return chartConfig.description();
  }

  @Override
  protected String params(DashboardContext context) {
    Map<String, String> params = new HashMap<>();
    params.put("chat_id", getChatId(context.relation()));
    if (chartConfig.paramsMapping() != null) {
      chartConfig
          .paramsMapping()
          .forEach(
              (key, path) ->
                  ReflectUtils.findValueByPathAsString(path, context)
                      .ifPresent(value -> params.put(key, value)));
    }
    return StringSubstitutor.replace(chartConfig.params, params, "%{", "}");
  }

  @Override
  public String datasetName() {
    return chartConfig.datasetName();
  }

  @Builder
  public record ChartConfig(
      String slice,
      String description,
      String datasetName,
      String params,
      Map<String, String> paramsMapping,
      String vizType) {}
}
