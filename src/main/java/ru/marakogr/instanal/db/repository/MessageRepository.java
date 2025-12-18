package ru.marakogr.instanal.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.MessageEntity;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, String> {
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO messages (
                primary_mid,
                timestamp,
                upsert_mid,
                sender_id,
                sender_name,
                upsert_timestamp,
                text,
                attachment_id,
                preview_small,
                preview_large,
                video_title,
                video_description,
                video_author,
                cta_url,
                object_id,
                upsert_date,
                has_reel,
                has_reaction,
                reaction_sender_id,
                reaction_sender_name,
                reaction,
                reaction_date,
                reaction_timestamp
            ) VALUES (
                :#{#e.primaryMid},
                :#{#e.timestamp},
                :#{#e.upsertMid},
                :#{#e.senderId},
                :#{#e.senderName},
                :#{#e.upsertTimestamp},
                :#{#e.text},
                :#{#e.attachmentId},
                :#{#e.previewSmall},
                :#{#e.previewLarge},
                :#{#e.videoTitle},
                :#{#e.videoDescription},
                :#{#e.videoAuthor},
                :#{#e.ctaUrl},
                :#{#e.objectId},
                :#{#e.upsertDate},
                :#{#e.hasReel},
                :#{#e.hasReaction},
                :#{#e.reactionSenderId},
                :#{#e.reactionSenderName},
                :#{#e.reaction},
                :#{#e.reactionDate},
                :#{#e.reactionTimestamp}
            )
            ON CONFLICT (primary_mid) DO UPDATE SET
                timestamp = EXCLUDED.timestamp,
                upsert_mid = EXCLUDED.upsert_mid,
                sender_id = EXCLUDED.sender_id,
                sender_name = EXCLUDED.sender_name,
                upsert_timestamp = EXCLUDED.upsert_timestamp,
                upsert_date = EXCLUDED.upsert_date,
                text = EXCLUDED.text,
                attachment_id = EXCLUDED.attachment_id,
                preview_small = EXCLUDED.preview_small,
                preview_large = EXCLUDED.preview_large,
                video_title = EXCLUDED.video_title,
                video_description = EXCLUDED.video_description,
                video_author = EXCLUDED.video_author,
                cta_url = EXCLUDED.cta_url,
                object_id = EXCLUDED.object_id,
                reaction_sender_id = EXCLUDED.reaction_sender_id,
                reaction_sender_name = EXCLUDED.reaction_sender_name,
                reaction = EXCLUDED.reaction,
                reaction_timestamp = EXCLUDED.reaction_timestamp,
                has_reel = EXCLUDED.has_reel,
                has_reaction = EXCLUDED.has_reaction,
                reaction_date = EXCLUDED.reaction_date
            """, nativeQuery = true)
    void upsert(MessageEntity e);

    long countByHasReelTrueAndSenderId(String friendId);

    List<MessageEntity> findAllByHasReelTrueAndSenderId(String friendId);

    long countByHasReactionTrueAndReactionSenderIdAndPrimaryMidIn(String ownerId, List<String> friendReelsMids);

}