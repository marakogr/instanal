package ru.marakogr.instanal.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marakogr.instanal.db.model.SuperUser;

import java.util.Optional;

public interface SuperUserRepository extends JpaRepository<SuperUser, Long> {
    Optional<SuperUser> findByInstagram(String instagram);

    SuperUser findByInstagramId(String instagramId);
}