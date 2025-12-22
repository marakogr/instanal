package ru.marakogr.instanal.service.superset.chart.impl;

import static ru.marakogr.instanal.Utils.MESSAGES_DATASET_ID;

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
public class ReelsWeeklyChart extends AbstractChart {

  protected ReelsWeeklyChart(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  protected String description() {
    return "Reels dynamics by week";
  }

  @Override
  protected String params(FriendRelation relation) {
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var ownerId = owner.getInstagramId();
    var friendId = friend.getInstagramId();
    var chatId = Utils.getChatId(ownerId, friendId);

    Map<String, Object> paramsMap = new HashMap<>();
    paramsMap.put("viz_type", "echarts_area");
    paramsMap.put("x_axis", "upsert_date");
    paramsMap.put("time_grain_sqla", "P1W");
    paramsMap.put("x_axis_sort_asc", true);
    paramsMap.put("x_axis_sort_series", "name");
    paramsMap.put("x_axis_sort_series_ascending", true);
    paramsMap.put(
        "metrics",
        List.of(
            Map.of(
                "aggregate", "COUNT",
                "column", Map.of("column_name", "sender_name"),
                "label", "COUNT(sender_name)",
                "expressionType", "SIMPLE")));
    paramsMap.put("groupby", List.of("sender_name"));
    paramsMap.put(
        "adhoc_filters",
        List.of(
            Map.of(
                "expressionType", "SIMPLE",
                "subject", "has_reel",
                "operator", "==",
                "operatorId", "IS_TRUE",
                "comparator", true,
                "clause", "WHERE"),
            Map.of(
                "expressionType", "SIMPLE",
                "subject", "chat_id",
                "operator", "==",
                "operatorId", "EQUALS",
                "comparator", chatId,
                "clause", "WHERE")));
    paramsMap.put("order_desc", true);
    paramsMap.put("row_limit", 10000);
    paramsMap.put("color_scheme", "supersetColors");
    paramsMap.put("seriesType", "line");
    paramsMap.put("opacity", 0.2);
    paramsMap.put("show_value", true);
    paramsMap.put("only_total", true);
    paramsMap.put("show_legend", true);
    paramsMap.put("legendType", "scroll");
    paramsMap.put("legendOrientation", "top");
    paramsMap.put("rich_tooltip", true);
    paramsMap.put("tooltipTimeFormat", "smart_date");
    paramsMap.put("y_axis_format", "SMART_NUMBER");

    try {
      return Constants.OBJECT_MAPPER.writeValueAsString(paramsMap);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize params", e);
    }
  }

  @Override
  protected String vizTyp() {
    return "echarts_area";
  }

  @Override
  protected String slice() {
    return "Amount of Reels per week";
  }

  @Override
  protected Long datasetId(List<DatasetInfo> datasets, FriendRelation relation) {
    return MESSAGES_DATASET_ID;
  }

  @Override
  public int order() {
    return 2;
  }
}
