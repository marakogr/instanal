package ru.marakogr.instanal.integration.superset;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.api.SecurityApi;
import ru.marakogr.instanal.integration.superset.client.ApiClient;
import ru.marakogr.instanal.integration.superset.config.SupersetProperties;
import ru.marakogr.instanal.integration.superset.model.GuestTokenPostRequest;
import ru.marakogr.instanal.integration.superset.model.SupersetResource;
import ru.marakogr.instanal.integration.superset.model.SupersetUser;

@Service
public class SupersetService {
  private final Map<Class<? extends ApiClient.Api>, ApiClient.Api> classApiMap = new HashMap<>();
  private final ApiClient client;
  private final SupersetProperties supersetProperties;

  public SupersetService(
      @Qualifier("supersetApiClient") ApiClient client, SupersetProperties supersetProperties) {
    this.client = client;
    this.supersetProperties = supersetProperties;
  }

  @SuppressWarnings("unchecked")
  public <T extends ApiClient.Api> T getApi(Class<T> aClass) {
    return (T) classApiMap.computeIfAbsent(aClass, client::buildClient);
  }

  public String generateGuestLink(FriendRelation relation) {
    var friendSuperUser = relation.getFriendSuperUser();
    var dashboardSlug = relation.getDashboardSlug();
    if (dashboardSlug == null) {
      throw new IllegalStateException(
          "Дашборд ещё не создан для друга " + friendSuperUser.getName());
    }

    var friendName = friendSuperUser.getName();
    var instagramId = friendSuperUser.getInstagramId();

    var getGuestTokenRequest =
        GuestTokenPostRequest.builder()
            .user(
                SupersetUser.builder()
                    .userName(instagramId)
                    .lastName(friendName)
                    .firstName(instagramId)
                    .build())
            .rls(Collections.emptyList())
            .resources(
                List.of(SupersetResource.builder().type("dashboard").id(dashboardSlug).build()))
            .build();

    String token;
    try {
      var securityApi = getApi(SecurityApi.class);
      token = securityApi.apiV1SecurityGuestTokenPost(getGuestTokenRequest).token();
    } catch (Exception e) {
      throw new RuntimeException("Не удалось сгенерировать guest_token для " + friendName, e);
    }

    return supersetProperties.getPublicUrl()
        + "/superset/dashboard/"
        + dashboardSlug
        + "/?standalone=1&guest_token="
        + token;
  }
}
