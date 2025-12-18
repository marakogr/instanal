package ru.marakogr.instanal.chat.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentCta {
    private String attachmentId;
    private String url;
}
