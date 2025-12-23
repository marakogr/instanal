package ru.marakogr.instanal.service.superset.chart.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.superset.chart.AbstractChartProvider;

@Component
public class MaxReelsPerDayChart extends AbstractChartProvider {

  protected MaxReelsPerDayChart(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  protected String description() {
    return "Maximum number of reels per day";
  }

  @Override
  protected String params(FriendRelation relation) {
    Map<String, Object> paramsMap = new HashMap<>();

    paramsMap.put("viz_type", "big_number_total");

    paramsMap.put(
        "metric",
        Map.of(
            "expressionType", "SIMPLE",
            "aggregate", "MAX",
            "column", Map.of("column_name", "total_reels_per_day"),
            "label", "MAX(total_reels_per_day)"));

    paramsMap.put(
        "adhoc_filters",
        List.of(
            Map.of(
                "expressionType", "SIMPLE",
                "subject", "upsert_date",
                "operator", "TEMPORAL_RANGE",
                "comparator", "No filter",
                "clause", "WHERE")));

    paramsMap.put("subheader", "Reels за день");
    paramsMap.put("header_font_size", 0.4);
    paramsMap.put("subheader_font_size", 0.15);
    paramsMap.put("y_axis_format", "SMART_NUMBER");
    paramsMap.put("time_format", "smart_date");
    paramsMap.put("extra_form_data", Map.of());

    try {
      return Constants.OBJECT_MAPPER.writeValueAsString(paramsMap);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize params", e);
    }
  }

  @Override
  protected String vizTyp() {
    return "big_number_total";
  }

  @Override
  protected String slice() {
    return "Max Reels for a day";
  }

  @Override
  public int order() {
    return 6;
  }

  @Override
  public String datasetName() {
    return "max_reels_by_day";
  }
}
