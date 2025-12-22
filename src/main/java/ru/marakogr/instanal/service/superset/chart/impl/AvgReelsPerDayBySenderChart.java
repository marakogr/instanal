package ru.marakogr.instanal.service.superset.chart.impl;

import static ru.marakogr.instanal.Utils.AVERAGE_DATASET_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.Utils;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;
import ru.marakogr.instanal.service.superset.chart.AbstractChart;

@Component
public class AvgReelsPerDayBySenderChart extends AbstractChart {

  protected AvgReelsPerDayBySenderChart(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  protected String description() {
    return "Average reels per day grouped by sender";
  }

  @Override
  protected String params(FriendRelation relation) {
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var chatId = Utils.getChatId(owner.getInstagramId(), friend.getInstagramId());

    Map<String, Object> paramsMap = new HashMap<>();

    paramsMap.put("viz_type", "pie");

    paramsMap.put("groupby", List.of("super_user_name"));

    paramsMap.put(
        "metric",
        Map.of(
            "expressionType", "SIMPLE",
            "aggregate", "AVG",
            "column", Map.of("column_name", "reels_count"),
            "label", "AVG(reels_count)"));

    paramsMap.put(
        "adhoc_filters",
        List.of(
            Map.of(
                "expressionType", "SIMPLE",
                "subject", "upsert_date",
                "operator", "TEMPORAL_RANGE",
                "comparator", "No filter",
                "clause", "WHERE"),
            Map.of(
                "expressionType", "SIMPLE",
                "subject", "chat_id",
                "operator", "==",
                "operatorId", "EQUALS",
                "comparator", chatId,
                "clause", "WHERE")));

    paramsMap.put("row_limit", 100);
    paramsMap.put("sort_by_metric", true);
    paramsMap.put("color_scheme", "supersetColors");

    paramsMap.put("show_legend", true);
    paramsMap.put("legendType", "scroll");
    paramsMap.put("legendOrientation", "top");

    paramsMap.put("label_type", "key_value");
    paramsMap.put("number_format", "SMART_NUMBER");
    paramsMap.put("date_format", "smart_date");

    paramsMap.put("show_labels", true);
    paramsMap.put("labels_outside", true);
    paramsMap.put("label_line", true);
    paramsMap.put("show_labels_threshold", 5);
    paramsMap.put("show_total", true);

    paramsMap.put("donut", true);
    paramsMap.put("outerRadius", 70);
    paramsMap.put("innerRadius", 30);

    paramsMap.put("extra_form_data", Map.of());

    try {
      return Constants.OBJECT_MAPPER.writeValueAsString(paramsMap);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize params", e);
    }
  }

  @Override
  protected String vizTyp() {
    return "pie";
  }

  @Override
  protected String slice() {
    return "Среднее количество Reels в день";
  }

  @Override
  public int order() {
    return 8;
  }

  @Override
  protected Long datasetId(List<DatasetInfo> datasets, FriendRelation relation) {
    return AVERAGE_DATASET_ID;
  }
}
