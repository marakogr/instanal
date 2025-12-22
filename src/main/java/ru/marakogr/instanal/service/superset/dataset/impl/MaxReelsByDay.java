package ru.marakogr.instanal.service.superset.dataset.impl;

import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.repository.FriendRelationRepository;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.model.DatasetInfo;
import ru.marakogr.instanal.service.superset.dataset.AbstractDataset;

@Component
public class MaxReelsByDay extends AbstractDataset {
  private final FriendRelationRepository friendRelationRepository;

  protected MaxReelsByDay(
      SupersetService supersetService, FriendRelationRepository friendRelationRepository) {
    super(supersetService);
    this.friendRelationRepository = friendRelationRepository;
  }

  @Override
  public DatasetInfo generate(FriendRelation relation) {
    DatasetInfo datasetInfo = super.generate(relation);
    relation.setMaxReelsByDayDatasetId(datasetInfo.getId());
    friendRelationRepository.save(relation);
    return datasetInfo;
  }

  @Override
  protected String tableName() {
    return "max_reels_by_day";
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
