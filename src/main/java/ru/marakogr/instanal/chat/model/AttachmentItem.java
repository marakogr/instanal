package ru.marakogr.instanal.chat.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentItem {
  private String attachmentId;
  private String objectId;
}
