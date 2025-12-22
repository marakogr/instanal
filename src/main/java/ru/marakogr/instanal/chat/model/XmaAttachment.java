package ru.marakogr.instanal.chat.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XmaAttachment {
  private String attachmentId;
  private String previewSmall;
  private String previewLarge;
  private String videoTitle;
  private String videoDescription;
  private String videoAuthor;
}
