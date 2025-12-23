package ru.marakogr.instanal.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dashboards")
@Getter
@Setter
public class Dashboard {

  @Id private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "relation_id", nullable = false)
  private FriendRelation relation;

  @Column(nullable = false)
  private String title;
}
