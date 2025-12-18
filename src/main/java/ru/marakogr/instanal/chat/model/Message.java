package ru.marakogr.instanal.chat.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String primaryMid;
    private long timestamp;

    private UpsertMessage upsertMessage;
    private XmaAttachment xmaAttachment;
    private AttachmentCta attachmentCta;
    private AttachmentItem attachmentItem;
    private Reaction reaction;
    private boolean hasReaction;
    private boolean hasReel;

    public Message(String primaryMid) {
        this.primaryMid = primaryMid;
    }

    public boolean hasText() {
        return upsertMessage != null
                && upsertMessage.getText() != null
                && !upsertMessage.getText().isBlank();
    }

    public boolean isVideo() {
        return xmaAttachment != null && attachmentCta != null;
    }

    public String sender() {
        return upsertMessage != null ? upsertMessage.getSenderId() : "unknown";
    }
}
