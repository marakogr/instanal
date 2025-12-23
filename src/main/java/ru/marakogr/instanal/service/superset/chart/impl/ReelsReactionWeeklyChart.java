package ru.marakogr.instanal.service.superset.chart.impl;

import static ru.marakogr.instanal.Utils.getChatId;

import org.springframework.stereotype.Component;
import ru.marakogr.instanal.db.model.FriendRelation;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.superset.chart.AbstractChartProvider;

@Component
public class ReelsReactionWeeklyChart extends AbstractChartProvider {
  @Override
  public int order() {
    return 3;
  }

  @Override
  public String datasetName() {
    return "messages";
  }

  public ReelsReactionWeeklyChart(SupersetService supersetService) {
    super(supersetService);
  }

  @Override
  protected String description() {
    return "Weekly count of reactions to Reels";
  }

  @Override
  protected String slice() {
    return "Weekly Reels reactions";
  }

  @Override
  protected String vizTyp() {
    return "echarts_area";
  }

  @Override
  protected String params(FriendRelation relation) {
    return """
                {
                  "datasource": "1__table",
                  "viz_type": "echarts_area",
                  "x_axis": "reaction_date",
                  "time_grain_sqla": "P1W",
                  "x_axis_sort_asc": true,
                  "x_axis_sort_series": "name",
                  "x_axis_sort_series_ascending": true,
                  "metrics": [
                    {
                      "aggregate": "COUNT",
                      "column": {
                        "column_name": "reaction_sender_name",
                        "type": "VARCHAR(255)",
                        "type_generic": 1,
                        "filterable": true,
                        "groupby": true
                      },
                      "expressionType": "SIMPLE",
                      "label": "COUNT(reaction_sender_name)"
                    }
                  ],
                  "groupby": ["reaction_sender_name"],
                  "adhoc_filters": [
                    {
                      "expressionType": "SIMPLE",
                      "subject": "has_reaction",
                      "operator": "==",
                      "operatorId": "IS_TRUE",
                      "comparator": true,
                      "clause": "WHERE"
                    },
                    {
                      "expressionType": "SIMPLE",
                      "subject": "has_reel",
                      "operator": "==",
                      "operatorId": "IS_TRUE",
                      "comparator": true,
                      "clause": "WHERE"
                    },
                    {
                      "expressionType": "SIMPLE",
                      "subject": "reaction_date",
                      "operator": "TEMPORAL_RANGE",
                      "comparator": "No filter",
                      "clause": "WHERE"
                    },
                    {
                      "expressionType": "SIMPLE",
                      "subject": "chat_id",
                      "operator": "==",
                      "operatorId": "EQUALS",
                      "comparator": "%s",
                      "clause": "WHERE"
                    }
                  ],
                  "order_desc": true,
                  "row_limit": 10000,
                  "truncate_metric": true,
                  "show_empty_columns": true,
                  "comparison_type": "values",
                  "annotation_layers": [],
                  "forecastEnabled": false,
                  "forecastPeriods": 10,
                  "forecastInterval": 0.8,
                  "sort_series_type": "sum",
                  "color_scheme": "supersetColors",
                  "seriesType": "line",
                  "opacity": 0.2,
                  "show_value": true,
                  "only_total": true,
                  "markerSize": 6,
                  "show_legend": true,
                  "legendType": "scroll",
                  "legendOrientation": "top",
                  "x_axis_time_format": "smart_date",
                  "rich_tooltip": true,
                  "tooltipTimeFormat": "smart_date",
                  "y_axis_format": "SMART_NUMBER",
                  "truncateXAxis": true,
                  "y_axis_bounds": [null, null],
                  "extra_form_data": {},
                  "dashboards": []
                }
                """
        .formatted(getChatId(relation));
  }
}
