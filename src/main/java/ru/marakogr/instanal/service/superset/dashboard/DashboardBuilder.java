package ru.marakogr.instanal.service.superset.dashboard;

import static ru.marakogr.instanal.utils.Utils.array;
import static ru.marakogr.instanal.utils.Utils.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;
import ru.marakogr.instanal.integration.superset.model.ChartInfo;
import ru.marakogr.instanal.integration.superset.model.DashboardCreateResponse;
import ru.marakogr.instanal.integration.superset.model.DashboardInfo;
import ru.marakogr.instanal.integration.superset.model.DashboardPostRequest;
import ru.marakogr.instanal.service.superset.chart.ChartService;

@Component
@RequiredArgsConstructor
public class DashboardBuilder {
  private final SupersetService supersetService;
  private final ChartService chartService;

  public DashboardInfo build(DashboardContext context) {
    var relation = context.relation();
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var owners = List.of(1L, owner.getSupersetUserId(), friend.getSupersetUserId());
    context =
        context.toBuilder()
            .owners(owners)
            .ownerId(owner.getInstagram())
            .friendId(friend.getInstagram())
            .build();
    var charts = chartService.get(context);
    context = context.toBuilder().charts(charts).build();
    var dashboard = createDashboard(context);
    chartService.addToDashboard(charts, dashboard.getId());
    return dashboard;
  }

  private DashboardInfo createDashboard(DashboardContext context) {
    var charts = context.charts();
    var relation = context.relation();
    var title = context.title();
    var owner = relation.getOwner();
    var friend = relation.getFriendSuperUser();
    var ownerName = owner.getInstagram();
    var friendName = friend.getInstagram();
    var owners = List.of(1L, owner.getSupersetUserId(), friend.getSupersetUserId());

    var request =
        DashboardPostRequest.builder()
            .dashboardTitle(title + " for " + ownerName + " and " + friendName)
            .slug("instanal-" + UUID.randomUUID())
            .owners(owners)
            .published(true)
            .css("")
            .positionJson(buildPositionJsonPerChartRow(charts, "Статистика чата"))
            .jsonMetadata(buildJsonMetadata(context))
            .build();

    var response = supersetService.getApi(DashboardsApi.class).apiV1DashboardPost(request);
    DashboardCreateResponse data = response.getData();
    return data.getResult().toBuilder().id(data.getId()).build();
  }

  private ObjectNode buildDateFilter(DashboardContext context) {
    var mapper = Constants.OBJECT_MAPPER;
    var chartsInScope = mapper.createArrayNode();
    context.charts().forEach(chart -> chartsInScope.add(chart.getId()));
    var targets = mapper.createArrayNode();
    targets.add(mapper.createObjectNode()); // <-- {}
    var timeRangeValue = context.supersetTimeRange();
    return object(
        "id", mapper.convertValue("NATIVE_FILTER-date_filter", JsonNode.class),
        "type", mapper.convertValue("NATIVE_FILTER", JsonNode.class),
        "name", mapper.convertValue("Date range", JsonNode.class),
        "filterType",
            mapper.convertValue("filter_time", JsonNode.class), // <-- как в рабочем примере
        "targets", targets,
        "defaultDataMask",
            object(
                "extraFormData",
                    object("time_range", mapper.convertValue(timeRangeValue, JsonNode.class)),
                "filterState",
                    object("value", mapper.convertValue(timeRangeValue, JsonNode.class))),
        "controlValues", object("enableEmptyFilter", true),
        "cascadeParentIds", mapper.createArrayNode(),
        "scope",
            object(
                "rootPath", array("ROOT_ID"),
                "excluded", mapper.createArrayNode()),
        "chartsInScope", chartsInScope,
        "tabsInScope", mapper.createArrayNode(),
        "description", mapper.convertValue("", JsonNode.class));
  }

  public String buildJsonMetadata(DashboardContext context) {
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

    for (var chart : context.charts()) {
      chartsInScope.add(chart.getId());
      chartConfig.set(
          String.valueOf(chart.getId()),
          object(
              "id", mapper.convertValue(chart.getId(), JsonNode.class),
              "crossFilters",
                  object(
                      "scope", mapper.convertValue("global", JsonNode.class),
                      "chartsInScope", mapper.createArrayNode())));
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

    if (context.hasDateFilter()) {
      root.putArray("native_filter_configuration").add(buildDateFilter(context));
    }

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
