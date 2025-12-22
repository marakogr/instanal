package ru.marakogr.instanal.db.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.SuperUser;

public interface FriendRelationRepository extends JpaRepository<FriendRelation, Long> {
  List<FriendRelation> findByOwnerId(Long ownerId);

  List<FriendRelation> findByOwner(SuperUser owner);

  List<FriendRelation> findByFriendSuperUserInstagramContainingIgnoreCase(String instagram);

  FriendRelation findByOwnerAndFriendSuperUser(SuperUser friendSuperUser, SuperUser superUser);
}
