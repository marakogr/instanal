package ru.marakogr.instanal.service;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.chat.model.Message;
import ru.marakogr.instanal.db.repository.MessageRepository;
import ru.marakogr.instanal.mapper.MessageMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageMapper messageMapper;
  private final MessageRepository messageRepository;

  @Transactional
  public void saveAll(Collection<Message> messages) {
    if (messages != null && !messages.isEmpty()) {
      log.info("saving messages, count: {}", messages.size());
      messages.stream().map(messageMapper::toEntity).forEach(messageRepository::upsert);
      log.info("done");
    }
  }
}
