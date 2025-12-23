package ru.marakogr.instanal.service.superset.dashboard;

import static ru.marakogr.instanal.Utils.array;
import static ru.marakogr.instanal.Utils.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.ChartPostRequest;
import ru.marakogr.instanal.integration.superset.model.DashboardPostRequest;
import ru.marakogr.instanal.service.superset.chart.ChartService;

@Component
@RequiredArgsConstructor
public class DashboardBuilder {
  private final SupersetService supersetService;
  private final ChartService chartService;

  public String build(FriendRelation relation, List<String> chartIds, String title) {
    var charts = chartService.get(relation, chartIds);
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var ownerName = owner.getInstagram();
    var friendName = friend.getInstagram();
    var owners = List.of(1L, owner.getSupersetUserId(), friend.getSupersetUserId());
    var dashboardId = createDashboard(title, ownerName, friendName, owners, charts);
    charts.forEach(chartInfo -> addToDashboard(chartInfo, Integer.parseInt(dashboardId)));
    return dashboardId;
  }

  private void addToDashboard(ChartInfo chartInfo, Integer dashboardId) {
    if (chartInfo instanceof ChartPostRequest request) {
      var chartRequest = request.toBuilder().dashboards(List.of(dashboardId)).build();
      supersetService.getApi(ChartApi.class).apiV1ChartPut(request.id(), chartRequest);
    }
  }

  private String createDashboard(
      String title,
      String ownerName,
      String friendName,
      List<Long> owners,
      List<ChartInfo> charts) {
    var request =
        DashboardPostRequest.builder()
            .dashboardTitle(title + " for " + ownerName + " and " + friendName)
            .slug("instanal-" + UUID.randomUUID())
            .owners(owners)
            .published(true)
            .css("")
            .positionJson(buildPositionJsonPerChartRow(charts, "Статистика чата"))
            .jsonMetadata(buildJsonMetadata(charts))
            .build();

    var response = supersetService.getApi(DashboardsApi.class).apiV1DashboardPost(request);
    return response.getData().getId();
  }

  public String buildJsonMetadata(List<ChartInfo> charts) {
    var mapper = Constants.OBJECT_MAPPER;
    var root = mapper.createObjectNode();

    root.put("refresh_frequency", 0);
    root.putArray("timed_refresh_immune_slices");
    root.set("expanded_slices", mapper.createObjectNode());
    root.put("default_filters", "{}");
    root.put("color_scheme", "");
    root.set("label_colors", mapper.createObjectNode());
    root.set("shared_label_colors", mapper.createObjectNode());
    root.put("cross_filters_enabled", false);

    var chartConfig = mapper.createObjectNode();
    var chartsInScope = mapper.createArrayNode();

    for (var chart : charts) {
      chartsInScope.add(chart.getId());

      chartConfig.set(
          String.valueOf(chart.getId()),
          object(
              "id",
              chart.getId(),
              "crossFilters",
              object("scope", "global", "chartsInScope", mapper.createArrayNode())));
    }

    root.set("chart_configuration", chartConfig);

    root.set(
        "global_chart_configuration",
        object(
            "scope",
            object(
                "rootPath", array("ROOT_ID"),
                "excluded", mapper.createArrayNode()),
            "chartsInScope",
            chartsInScope));

    try {
      return mapper.writeValueAsString(root);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String buildPositionJsonPerChartRow(List<ChartInfo> charts, String headerText) {
    ObjectMapper mapper = Constants.OBJECT_MAPPER;
    ObjectNode root = mapper.createObjectNode();

    root.put("DASHBOARD_VERSION_KEY", "v2");

    root.set(
        "HEADER_ID",
        object(
            "id", "HEADER_ID",
            "type", "HEADER",
            "meta", object("text", headerText)));

    root.set(
        "ROOT_ID",
        object(
            "id", "ROOT_ID",
            "type", "ROOT",
            "children", array("GRID_ID")));

    ArrayNode gridChildren = mapper.createArrayNode();

    root.set(
        "GRID_ID",
        object(
            "id",
            "GRID_ID",
            "type",
            "GRID",
            "parents",
            array("ROOT_ID"),
            "children",
            gridChildren));

    for (int i = 0; i < charts.size(); i++) {
      ChartInfo chart = charts.get(i);

      String rowId = "ROW-" + (i + 1);
      String chartId = "CHART-" + (i + 1);

      gridChildren.add(rowId);

      root.set(
          rowId,
          object(
              "id", rowId,
              "type", "ROW",
              "parents", array("ROOT_ID", "GRID_ID"),
              "children", array(chartId),
              "meta", object("background", "BACKGROUND_TRANSPARENT")));

      root.set(
          chartId,
          object(
              "id",
              chartId,
              "type",
              "CHART",
              "parents",
              array("ROOT_ID", "GRID_ID", rowId),
              "children",
              mapper.createArrayNode(),
              "meta",
              object(
                  "chartId", chart.getId(),
                  "width", 12,
                  "height", 50,
                  "sliceName", chart.getSlice(),
                  "uuid", UUID.randomUUID().toString())));
    }

    try {
      return mapper.writeValueAsString(root);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
