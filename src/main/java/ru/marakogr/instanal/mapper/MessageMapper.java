package ru.marakogr.instanal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.marakogr.instanal.chat.model.Message;
import ru.marakogr.instanal.db.model.MessageEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "primaryMid", source = "primaryMid")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "upsertMid", source = "upsertMessage.mid")
    @Mapping(target = "senderId", source = "upsertMessage.senderId")
    @Mapping(target = "senderName", source = "upsertMessage.senderName")
    @Mapping(target = "upsertTimestamp", source = "upsertMessage.timestamp")
    @Mapping(target = "text", source = "upsertMessage.text")
    @Mapping(target = "upsertDate", source = "upsertMessage.date")
    @Mapping(target = "attachmentId", source = "xmaAttachment.attachmentId")
    @Mapping(target = "previewSmall", source = "xmaAttachment.previewSmall")
    @Mapping(target = "previewLarge", source = "xmaAttachment.previewLarge")
    @Mapping(target = "videoTitle", source = "xmaAttachment.videoTitle")
    @Mapping(target = "videoDescription", source = "xmaAttachment.videoDescription")
    @Mapping(target = "videoAuthor", source = "xmaAttachment.videoAuthor")
    @Mapping(target = "ctaUrl", source = "attachmentCta.url")
    @Mapping(target = "objectId", source = "attachmentItem.objectId")
    @Mapping(target = "reactionSenderId", source = "reaction.senderId")
    @Mapping(target = "reactionSenderName", source = "reaction.senderNickname")
    @Mapping(target = "reaction", source = "reaction.reaction")
    @Mapping(target = "reactionTimestamp", source = "reaction.timestamp")
    @Mapping(target = "reactionDate", source = "reaction.reactionDate")
    @Mapping(target = "hasReaction", source = "hasReaction")
    @Mapping(target = "hasReel", source = "hasReel")
    MessageEntity toEntity(Message message);

    List<MessageEntity> toEntities(List<Message> messages);
}
