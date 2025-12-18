package ru.marakogr.instanal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.model.MessageEntity;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.db.repository.FriendRelationRepository;
import ru.marakogr.instanal.db.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final FriendRelationRepository friendRelationRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public void analyzeAllFriends(SuperUser owner) {
        List<FriendRelation> relations = friendRelationRepository.findByOwner(owner);

        for (FriendRelation relation : relations) {
            var friend = relation.getFriendSuperUser();
            var ownerId = owner.getInstagramId();
            var friendId = friend.getInstagramId();
            if (friendId == null || ownerId == null) {
                relation.setRating(0.0);
                continue;
            }
            long reelsFromFriend = messageRepository.countByHasReelTrueAndSenderId(friendId);
            var friendReelsMids = messageRepository.findAllByHasReelTrueAndSenderId(friendId)
                    .stream()
                    .map(MessageEntity::getPrimaryMid)
                    .toList();
            double myReactionsOnFriendReels = 0;
            if (!friendReelsMids.isEmpty()) {
                myReactionsOnFriendReels = messageRepository.countByHasReactionTrueAndReactionSenderIdAndPrimaryMidIn(
                        ownerId, friendReelsMids);
            }
            long reelsFromMe = messageRepository.countByHasReelTrueAndSenderId(ownerId);
            var myReelsMids = messageRepository.findAllByHasReelTrueAndSenderId(ownerId)
                    .stream()
                    .map(MessageEntity::getPrimaryMid)
                    .collect(Collectors.toList());
            double hisReactionsOnMyReels = 0;
            if (!myReelsMids.isEmpty()) {
                hisReactionsOnMyReels = messageRepository.countByHasReactionTrueAndReactionSenderIdAndPrimaryMidIn(
                        friendId, myReelsMids);
            }
            double finalRating = getFinalRating(reelsFromFriend, myReactionsOnFriendReels, reelsFromMe, hisReactionsOnMyReels);
            relation.setRating(finalRating);
            friendRelationRepository.save(relation);
        }
    }

    private static double getFinalRating(long reelsFromFriend,
                                         double myReactionsOnFriendReels,
                                         long reelsFromMe,
                                         double hisReactionsOnMyReels) {
        double baseScore = reelsFromFriend * 0.005 + myReactionsOnFriendReels * 0.007;
        double baseRating = Math.min(10.0, baseScore);
        double mutualRatio = (reelsFromMe > 0) ? hisReactionsOnMyReels / reelsFromMe : 1.0;
        double mutualBonus = switch ((int) (mutualRatio * 10)) {
            case 9, 10 -> 1.5;
            case 7, 8 -> 0.8;
            case 5, 6 -> 0.0;
            case 3, 4 -> -1.0;
            default -> -2.0;
        };
        double finalRating = Math.max(0.0, Math.min(10.0, baseRating + mutualBonus));
        finalRating = Math.round(finalRating * 10) / 10.0;
        return finalRating;
    }
}
