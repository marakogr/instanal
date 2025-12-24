package ru.marakogr.instanal.service.superset.dashboard;

import static ru.marakogr.instanal.integration.superset.GetListSchemaDsl.singleFilter;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;
import ru.marakogr.instanal.integration.superset.api.SecurityApi;
import ru.marakogr.instanal.integration.superset.config.SupersetProperties;
import ru.marakogr.instanal.integration.superset.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private final SupersetService supersetService;
  private final DashboardBuilder dashboardBuilder;
  private final SupersetProperties supersetProperties;

  @Override
  @Transactional(readOnly = true)
  public List<DashboardInfo> findByRelation(FriendRelation relation) {
    var filter =
        singleFilter(
            "owners", FilterOperator.REL, relation.getOwner().getSupersetUserId(), 0, 1000);

    return supersetService
        .getApi(DashboardsApi.class)
        .apiV1DashboardGet(filter)
        .getData()
        .getResult()
        .stream()
        .map(DashboardInfo.class::cast)
        .toList()
        .stream()
        .filter(
            dashboardInfo ->
                dashboardInfo
                    .getOwners()
                    .contains(relation.getFriendSuperUser().getSupersetUserId()))
        .toList();
  }

  @Override
  @Transactional
  public DashboardInfo createDashboard(
      FriendRelation relation, String title, List<String> chartIds) {
    Map<String, Set<String>> supersetToId = new HashMap<>();
    try {
      var dashboard = dashboardBuilder.build(relation, chartIds, title);
      supersetToId
          .computeIfAbsent("dashboard", k -> new HashSet<>())
          .add(dashboard.getId().toString());
      return dashboard;
    } catch (Exception e) {
      log.error("error during dashboard creation: {}", e.getMessage());
      supersetService.rollbackSuperset(supersetToId);
      throw e;
    }
  }

  @Override
  public String generateGuestLink(FriendRelation relation, DashboardInfo dashboard) {
    var friendSuperUser = relation.getFriendSuperUser();
    var dashboardSlug = dashboard.getSlug();
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
      token =
          supersetService
              .getApi(SecurityApi.class)
              .apiV1SecurityGuestTokenPost(getGuestTokenRequest)
              .token();
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
