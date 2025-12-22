package ru.marakogr.instanal.chat.model;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
  private String senderId;
  private String senderNickname;
  private String reaction;
  private long timestamp;
  private LocalDate reactionDate;
}
