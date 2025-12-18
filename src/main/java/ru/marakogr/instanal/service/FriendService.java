package ru.marakogr.instanal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.db.repository.FriendRelationRepository;
import ru.marakogr.instanal.db.repository.SuperUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRelationRepository relationRepo;
    private final SuperUserRepository userRepo;

    @Transactional
    public FriendRelation addFriend(SuperUser owner, String name, String instagram, String instagramId, String telegram) {
        var friend = userRepo.findByInstagram(instagram)
                .map(existing -> {
                    existing.setName(name);
                    existing.setInstagramId(instagramId); // теперь обязательное поле
                    existing.setTelegram(telegram);
                    return userRepo.save(existing);
                })
                .orElseGet(() -> {
                    return userRepo.save(
                            SuperUser.builder()
                                    .name(name)
                                    .instagram(instagram)
                                    .instagramId(instagramId)
                                    .telegram(telegram)
                                    .build()
                    );
                });

        var relation = new FriendRelation();
        relation.setOwner(owner);
        relation.setFriendSuperUser(friend);

        return relationRepo.save(relation);
    }

    public List<FriendRelation> getFriends(SuperUser superUser) {
        return relationRepo.findByOwnerId(superUser.getId());
    }

    public void updateRating(FriendRelation relation, double rating) {
        relation.setRating(rating);
        relationRepo.save(relation);
    }

    public void deleteFriend(FriendRelation friendRelation) {
        relationRepo.delete(friendRelation);
    }
}
