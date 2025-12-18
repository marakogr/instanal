package ru.marakogr.instanal.chat.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertMessage {
    private String mid;
    private String senderId;
    private String senderName;
    private long timestamp;
    private LocalDate date;
    private String text;
}
