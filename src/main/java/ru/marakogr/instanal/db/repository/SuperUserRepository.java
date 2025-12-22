package ru.marakogr.instanal.db.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.marakogr.instanal.db.model.SuperUser;

public interface SuperUserRepository extends JpaRepository<SuperUser, Long> {
  Optional<SuperUser> findByInstagram(String instagram);

  List<SuperUser> findByInstagramContainingIgnoreCase(String instagram);
}
