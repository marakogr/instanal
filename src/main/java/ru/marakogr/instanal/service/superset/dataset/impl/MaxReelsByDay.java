package ru.marakogr.instanal.service.superset.dataset.impl;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.superset.dataset.AbstractDatasetProvider;
import ru.marakogr.instanal.service.superset.dataset.DatasetService;

@Component
public class MaxReelsByDay extends AbstractDatasetProvider {

  protected MaxReelsByDay(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  public String name() {
    return "max_reels_by_day";
  }

  @Override
  public DatasetService.DatasetType type() {
    return DatasetService.DatasetType.PERSONAL;
  }

  @Override
  protected String sql(String chatId, String ownerId, String friendId) {
    Map<String, String> params =
        Map.of(
            "chatId", chatId,
            "ownerId", ownerId,
            "friendId", friendId);
    var template =
        """
                WITH daily_reels AS (
                  SELECT
                    upsert_date,
                    COUNT(*) AS total_reels_per_day,
                    COUNT(*) FILTER (WHERE sender_id = '%{friendId}') AS reels_count_%{friendId},
                    COUNT(*) FILTER (WHERE sender_id = '%{ownerId}') AS reels_count_%{ownerId}
                  FROM messages
                  WHERE has_reel = true
                    AND chat_id = '%{chatId}'
                  GROUP BY upsert_date
                )
                SELECT
                  upsert_date,
                  total_reels_per_day,
                  reels_count_%{friendId},
                  reels_count_%{ownerId}
                FROM daily_reels
                ORDER BY total_reels_per_day DESC, upsert_date;
                """;
    return StringSubstitutor.replace(template, params, "%{", "}");
  }
}
