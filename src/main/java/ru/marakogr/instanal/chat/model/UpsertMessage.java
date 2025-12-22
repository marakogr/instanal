package ru.marakogr.instanal.chat.model;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertMessage {
  private String mid;
  private String senderId;
  private String chatId;
  private String senderName;
  private long timestamp;
  private LocalDate date;
  private String text;
}
