package ru.marakogr.instanal.service.superset;

import static ru.marakogr.instanal.integration.superset.GetListSchemaDsl.*;
import static ru.marakogr.instanal.integration.superset.model.FilterOperator.EQ;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.db.model.SuperUser;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.UsersApi;
import ru.marakogr.instanal.integration.superset.model.*;

@Service
public class SupersetUsersService {
  private final UsersApi usersApi;

  public SupersetUsersService(SupersetService service) {
    this.usersApi = service.getApi(UsersApi.class);
  }

  public Long getOrCreate(SuperUser user) {
    var filter = singleFilter("username", EQ, user.getInstagramId(), 0, 1);
    var response = usersApi.apiV1UsersGet(filter);
    return Optional.ofNullable(response)
        .map(ApiResponse::getData)
        .map(IdArrayWrapper::getResult)
        .filter(r -> r.size() == 1)
        .map(List::getFirst)
        .map(IdWrapper::getId)
        .map(Long::parseLong)
        .orElseGet(
            () -> {
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
                  .map(Long::parseLong)
                  .orElseThrow(() -> new RuntimeException("error during user creation!"));
            });
  }
}
