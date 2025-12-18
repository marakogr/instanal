package ru.marakogr.instanal.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.SuperUser;

import java.util.List;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {
    List<FriendRelation> findByOwnerId(Long ownerId);

    List<FriendRelation> findByOwner(SuperUser owner);
}
