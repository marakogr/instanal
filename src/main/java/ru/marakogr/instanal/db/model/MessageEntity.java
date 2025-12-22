package ru.marakogr.instanal.db.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(
    name = "messages",
    uniqueConstraints =
        @UniqueConstraint(name = "uq_messages_primary_mid", columnNames = "primary_mid"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

  @Id
  @Column(name = "primary_mid", nullable = false, updatable = false)
  private String primaryMid;

  private long timestamp;

  /* -------- UpsertMessage -------- */

  @Column(name = "upsert_mid")
  private String upsertMid;

  @Column(name = "chat_id")
  private String chatId;

  @Column(name = "sender_id")
  private String senderId;

  @Column(name = "sender_name")
  private String senderName;

  @Column(name = "upsert_timestamp")
  private Long upsertTimestamp;

  @Column(name = "text", columnDefinition = "text")
  private String text;

  @Column(name = "upsert_date")
  private LocalDate upsertDate;

  /* -------- XMA -------- */

  @Column(name = "attachment_id")
  private String attachmentId;

  @Column(name = "preview_small", columnDefinition = "text")
  private String previewSmall;

  @Column(name = "preview_large", columnDefinition = "text")
  private String previewLarge;

  @Column(name = "video_title", length = 512)
  private String videoTitle;

  @Column(name = "video_description", columnDefinition = "text")
  private String videoDescription;

  @Column(name = "video_author")
  private String videoAuthor;

  /* -------- CTA / Item -------- */

  @Column(name = "cta_url")
  private String ctaUrl;

  @Column(name = "object_id")
  private String objectId;

  /* -------- Reaction -------- */

  @Column(name = "reaction_sender_id")
  private String reactionSenderId;

  @Column(name = "reaction_sender_name")
  private String reactionSenderName;

  private String reaction;

  @Column(name = "reaction_timestamp")
  private Long reactionTimestamp;

  @Column(name = "reaction_date")
  private LocalDate reactionDate;

  /* -------- Aggregation helpers -------- */

  @Column(name = "has_reaction", nullable = false)
  private boolean hasReaction;

  @Column(name = "has_reel", nullable = false)
  private boolean hasReel;
}
