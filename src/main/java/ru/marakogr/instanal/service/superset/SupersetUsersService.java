package ru.marakogr.instanal.service.superset;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.UsersApi;
import ru.marakogr.instanal.integration.superset.model.ApiResponse;
import ru.marakogr.instanal.integration.superset.model.IdWrapper;
import ru.marakogr.instanal.integration.superset.model.UserPostRequest;

@Service
public class SupersetUsersService {
  private final UsersApi usersApi;

  public SupersetUsersService(SupersetService service) {
    this.usersApi = service.getApi(UsersApi.class);
  }

  public Optional<Long> ensureUserExists(SuperUser user) {
    try {
      var supersetUser = usersApi.apiV1UserByIdGet(user.getSupersetUserId(), null);
      if (supersetUser.getStatusCode() == 200) {
        return Optional.of(supersetUser)
            .map(ApiResponse::getData)
            .map(IdWrapper::getId)
            .map(Long::parseLong);
      }
    } catch (Exception e) {
      // не найден
    }

    return createSupersetUser(user);
  }

  public Optional<Long> createSupersetUser(SuperUser user) {
    var userPostRequest =
        UserPostRequest.builder()
            .userName(user.getInstagramId())
            .firstName(user.getName())
            .lastName(user.getInstagramId())
            .email(user.getInstagram())
            .active(true)
            .password(user.getInstagramId())
            .roles(List.of(4L))
            .build();
    var userResponse = usersApi.apiV1UserPost(userPostRequest);
    return Optional.ofNullable(userResponse)
        .map(ApiResponse::getData)
        .map(IdWrapper::getId)
        .map(Long::parseLong);
  }
}
