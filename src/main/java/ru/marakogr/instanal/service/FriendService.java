package ru.marakogr.instanal.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.db.repository.FriendRelationRepository;

@Service
@RequiredArgsConstructor
public class FriendService {
  private final FriendRelationRepository relationRepo;
  private final SuperUserService superUserService;

  @Transactional
  public FriendRelation addFriend(
      SuperUser owner, String name, String instagram, String instagramId, String telegram) {
    var friend =
        superUserService
            .findByInstagram(instagram)
            .orElseGet(() -> createUser(name, instagram, instagramId, telegram));
    return addFriend(owner, friend);
  }

  @Transactional
  public FriendRelation addFriend(SuperUser owner, SuperUser friend) {
    var relation = new FriendRelation();
    relation.setOwner(owner);
    relation.setFriendSuperUser(friend);
    relationRepo.save(relation);
    return relation;
  }

  @Transactional(readOnly = true)
  public List<FriendRelation> getFriends(SuperUser superUser) {
    return relationRepo.findByOwnerId(superUser.getId());
  }

  @Transactional
  public void deleteFriend(FriendRelation friendRelation) {
    relationRepo.delete(friendRelation);
  }

  private SuperUser createUser(String name, String instagram, String instagramId, String telegram) {
    return superUserService.createUser(
        SuperUser.builder()
            .name(name)
            .instagram(instagram)
            .instagramId(instagramId)
            .telegram(telegram)
            .build());
  }
}
