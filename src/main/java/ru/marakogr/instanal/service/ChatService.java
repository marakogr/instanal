package ru.marakogr.instanal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.chat.WebsocketInstagramDirectParser;
import ru.marakogr.instanal.chat.model.Message;
import ru.marakogr.instanal.db.model.FriendRelation;

import java.util.Optional;

import static ru.marakogr.instanal.chat.Constants.OBJECT_MAPPER;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageService messageService;

    public void importChat(byte[] harBytes, FriendRelation relation) {
        try {
            var har = OBJECT_MAPPER.readTree(harBytes);
            var friendId = relation.getFriendSuperUser().getInstagramId();
            var ownerId = relation.getOwner().getInstagramId();
            if (friendId == null || ownerId == null) {
                throw new IllegalArgumentException("Instagram ID is null for friend: " + relation.getFriendSuperUser().getId());
            }
            var directParser = new WebsocketInstagramDirectParser(har, friendId);
            var messages = directParser.getMessages();
            messages.forEach(message -> setSenderName(relation, message, friendId, ownerId));
            messageService.saveAll(messages);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setSenderName(FriendRelation relation, Message message, String friendId, String ownerId) {
        Optional.ofNullable(message)
                .map(Message::getReaction)
                .ifPresent(reaction -> {
                    String name = null;
                    var senderId = reaction.getSenderId();
                    if (senderId.equals(friendId)) {
                        name = relation.getFriendSuperUser().getName();
                    } else if (senderId.equals(ownerId)) {
                        name = relation.getOwner().getName();
                    }
                    reaction.setSenderNickname(name);
                });
        Optional.ofNullable(message)
                .map(Message::sender)
                .ifPresent(sender -> {
                    String name = null;
                    if (sender.equals(friendId)) {
                        name = relation.getFriendSuperUser().getName();
                    } else if (sender.equals(ownerId)) {
                        name = relation.getOwner().getName();
                    }
                    message.getUpsertMessage().setSenderName(name);
                });
    }
}
