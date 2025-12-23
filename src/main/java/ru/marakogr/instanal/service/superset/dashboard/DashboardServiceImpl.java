package ru.marakogr.instanal.service.superset.dashboard;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.marakogr.instanal.db.model.Dashboard;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.db.repository.DashboardRepository;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.SecurityApi;
import ru.marakogr.instanal.integration.superset.config.SupersetProperties;
import ru.marakogr.instanal.integration.superset.model.GuestTokenPostRequest;
import ru.marakogr.instanal.integration.superset.model.SupersetResource;
import ru.marakogr.instanal.integration.superset.model.SupersetUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
  private final SupersetService supersetService;
  private final DashboardBuilder dashboardBuilder;
  private final SupersetProperties supersetProperties;
  private final DashboardRepository dashboardRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Dashboard> findByRelation(FriendRelation relation) {
    return dashboardRepository.findByRelation(relation);
  }

  @Override
  @Transactional
  public Dashboard createDashboard(FriendRelation relation, String title, List<String> chartIds) {
    Map<String, Set<String>> supersetToId = new HashMap<>();
    try {
      var dashboardId = dashboardBuilder.build(relation, chartIds, title);
      supersetToId.computeIfAbsent("dashboard", k -> new HashSet<>()).add(dashboardId);
      var dashboard = new Dashboard();
      dashboard.setId(dashboardId);
      dashboard.setTitle(title);
      dashboard.setRelation(relation);
      return dashboardRepository.save(dashboard);
    } catch (Exception e) {
      log.error("error during dashboard creation: {}", e.getMessage());
      supersetService.rollbackSuperset(supersetToId);
      throw e;
    }
  }

  @Override
  public String generateGuestLink(FriendRelation relation, Dashboard dashboard) {
    var friendSuperUser = relation.getFriendSuperUser();
    var dashboardSlug = dashboard.getId();
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
