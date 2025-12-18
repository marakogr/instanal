package ru.marakogr.instanal.db.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRelation {
    @Id
    @SequenceGenerator(
            name = "friend_relation_seq",
            sequenceName = "friend_relation_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "friend_relation_seq"
    )
    private Long id;
    @ManyToOne
    private SuperUser owner;
    @ManyToOne
    private SuperUser friendSuperUser;
    private double rating = 0.0;
}