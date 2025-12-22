package ru.marakogr.instanal.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.db.repository.SuperUserRepository;
import ru.marakogr.instanal.service.superset.SupersetUsersService;

@Service
@RequiredArgsConstructor
public class SuperUserService {
  private final SuperUserRepository repository;
  private final SupersetUsersService supersetUsersService;

  @Transactional
  public SuperUser registerOrLogin(
      String name, String instagram, String instagramId, String telegram) {
    return repository
        .findByInstagram(instagram)
        .map(
            existing -> {
              existing.setName(name);
              existing.setTelegram(telegram);
              existing.setInstagramId(instagramId);
              return existing;
            })
        .orElseGet(() -> createUser(name, instagram, instagramId, telegram));
  }

  @Transactional
  public SuperUser createUser(String name, String instagram, String instagramId, String telegram) {
    var user =
        SuperUser.builder()
            .name(name)
            .instagram(instagram)
            .instagramId(instagramId)
            .telegram(telegram)
            .build();
    return createUser(user);
  }

  @Transactional(readOnly = true)
  public Optional<SuperUser> findByInstagram(String instagram) {
    return repository.findByInstagram(instagram);
  }

  @Transactional(readOnly = true)
  public List<SuperUser> findByInstagramContainingIgnoreCase(String query) {
    return repository.findByInstagramContainingIgnoreCase(query);
  }

  @Transactional
  public SuperUser createUser(SuperUser user) {
    supersetUsersService.createSupersetUser(user).ifPresent(user::setSupersetUserId);
    user = repository.save(user);
    return user;
  }
}
