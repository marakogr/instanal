package ru.marakogr.instanal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.db.repository.SuperUserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuperUserService {
    private final SuperUserRepository repository;

    @Transactional
    public SuperUser registerOrLogin(
            String name,
            String instagram,
            String instagramId,
            String telegram
    ) {
        return repository.findByInstagram(instagram)
                .map(existing -> {
                    existing.setName(name);
                    existing.setTelegram(telegram);
                    existing.setInstagramId(instagramId);
                    return existing;
                })
                .orElseGet(() ->
                        repository.save(
                                SuperUser.builder()
                                        .name(name)
                                        .instagram(instagram)
                                        .instagramId(instagramId)
                                        .telegram(telegram)
                                        .build()
                        )
                );
    }

    public String findNameByInstagramId(String id) {
        return repository.findByInstagramId(id).getName();
    }

    public Optional<SuperUser> loginByInstagram(String instagram) {
        return repository.findByInstagram(instagram);
    }
}