package ru.marakogr.instanal.integration.superset.model;

import java.util.Collections;
import java.util.List;

public final class GetListSchemaDsl {

  private GetListSchemaDsl() {}

  private static GetListSchema singleFilter(
      String column, String operator, Object value, int page, int pageSize) {
    return GetListSchema.builder()
        .filters(
            List.of(
                GetListSchemaFiltersInner.builder().col(column).opr(operator).value(value).build()))
        .page(page)
        .columns(Collections.emptyList())
        .keys(Collections.emptyList())
        .pageSize(pageSize)
        .build();
  }

  public static GetListSchema singleFilter(
      String column, FilterOperator operator, Object value, int page, int pageSize) {
    return singleFilter(column, operator.value(), value, page, pageSize);
  }
}
