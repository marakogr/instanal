package ru.marakogr.instanal.db.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "super_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "instagram"),
                @UniqueConstraint(columnNames = "instagram_id")
        }
)
public class SuperUser {

    @Id
    @SequenceGenerator(
            name = "super_user_seq",
            sequenceName = "super_user_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "super_user_seq"
    )
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String instagram;

    @Column(name = "instagram_id", nullable = false, unique = true)
    private String instagramId;

    private String telegram;
}
