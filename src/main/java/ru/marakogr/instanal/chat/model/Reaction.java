package ru.marakogr.instanal.chat.model;

import lombok.*;

import java.time.LocalDate;

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
