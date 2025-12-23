package ru.marakogr.instanal.service.superset.chart.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.Utils;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.superset.chart.AbstractChartProvider;

@Component
public class TotalReelsCountChart extends AbstractChartProvider {

  protected TotalReelsCountChart(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  protected String description() {
    return "Total number of reels for the selected period";
  }

  @Override
  protected String params(FriendRelation relation) {
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var chatId = Utils.getChatId(owner.getInstagramId(), friend.getInstagramId());

    Map<String, Object> paramsMap = new HashMap<>();

    paramsMap.put("viz_type", "big_number_total");
    paramsMap.put("metric", "count");

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

    paramsMap.put("subheader", "Reels за период");
    paramsMap.put("header_font_size", 0.3);
    paramsMap.put("subheader_font_size", 0.125);
    paramsMap.put("y_axis_format", "SMART_NUMBER");
    paramsMap.put("time_format", "smart_date");
    paramsMap.put("conditional_formatting", List.of());
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
    return "Total number of reels for the 2025 year";
  }

  @Override
  public String datasetName() {
    return "messages";
  }

  @Override
  public int order() {
    return 1;
  }
}
