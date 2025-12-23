package ru.marakogr.instanal.integration.superset.model;

import static ru.marakogr.instanal.chat.Constants.OBJECT_MAPPER;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;

/** GetListSchema */
@JsonPropertyOrder({
  GetListSchema.JSON_PROPERTY_COLUMNS,
  GetListSchema.JSON_PROPERTY_FILTERS,
  GetListSchema.JSON_PROPERTY_KEYS,
  GetListSchema.JSON_PROPERTY_PAGE,
  GetListSchema.JSON_PROPERTY_PAGE_SIZE
})
@JsonTypeName("get_list_schema")
public class GetListSchema {
  public static final String JSON_PROPERTY_COLUMNS = "columns";
  @javax.annotation.Nullable private List<String> columns = new ArrayList<>();

  public static final String JSON_PROPERTY_FILTERS = "filters";
  @javax.annotation.Nullable private List<GetListSchemaFiltersInner> filters = new ArrayList<>();

  /** Gets or Sets keys */
  public enum KeysEnum {
    LIST_COLUMNS("list_columns"),
    ORDER_COLUMNS("order_columns"),
    LABEL_COLUMNS("label_columns"),
    DESCRIPTION_COLUMNS("description_columns"),
    LIST_TITLE("list_title"),
    NONE("none");

    private final String value;

    KeysEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @JsonCreator
    public static KeysEnum fromValue(String value) {
      for (KeysEnum b : KeysEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String JSON_PROPERTY_KEYS = "keys";
  @javax.annotation.Nullable private List<KeysEnum> keys = new ArrayList<>();

  public static final String JSON_PROPERTY_PAGE = "page";
  @javax.annotation.Nullable private Integer page;

  public static final String JSON_PROPERTY_PAGE_SIZE = "page_size";
  @javax.annotation.Nullable private Integer pageSize;

  public GetListSchema() {}

  public GetListSchema columns(@javax.annotation.Nullable List<String> columns) {

    this.columns = columns;
    return this;
  }

  public GetListSchema addColumnsItem(String columnsItem) {
    if (this.columns == null) {
      this.columns = new ArrayList<>();
    }
    this.columns.add(columnsItem);
    return this;
  }

  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_COLUMNS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<String> getColumns() {
    return columns;
  }

  @JsonProperty(value = JSON_PROPERTY_COLUMNS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setColumns(@javax.annotation.Nullable List<String> columns) {
    this.columns = columns;
  }

  public GetListSchema filters(@javax.annotation.Nullable List<GetListSchemaFiltersInner> filters) {

    this.filters = filters;
    return this;
  }

  public GetListSchema addFiltersItem(GetListSchemaFiltersInner filtersItem) {
    if (this.filters == null) {
      this.filters = new ArrayList<>();
    }
    this.filters.add(filtersItem);
    return this;
  }

  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_FILTERS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<GetListSchemaFiltersInner> getFilters() {
    return filters;
  }

  @JsonProperty(value = JSON_PROPERTY_FILTERS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setFilters(@javax.annotation.Nullable List<GetListSchemaFiltersInner> filters) {
    this.filters = filters;
  }

  public GetListSchema keys(@javax.annotation.Nullable List<KeysEnum> keys) {

    this.keys = keys;
    return this;
  }

  public GetListSchema addKeysItem(KeysEnum keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_KEYS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public List<KeysEnum> getKeys() {
    return keys;
  }

  @JsonProperty(value = JSON_PROPERTY_KEYS, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setKeys(@javax.annotation.Nullable List<KeysEnum> keys) {
    this.keys = keys;
  }

  public GetListSchema page(@javax.annotation.Nullable Integer page) {

    this.page = page;
    return this;
  }

  /**
   * Get page
   *
   * @return page
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_PAGE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getPage() {
    return page;
  }

  @JsonProperty(value = JSON_PROPERTY_PAGE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPage(@javax.annotation.Nullable Integer page) {
    this.page = page;
  }

  public GetListSchema pageSize(@javax.annotation.Nullable Integer pageSize) {

    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   *
   * @return pageSize
   */
  @javax.annotation.Nullable
  @JsonProperty(value = JSON_PROPERTY_PAGE_SIZE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public Integer getPageSize() {
    return pageSize;
  }

  @JsonProperty(value = JSON_PROPERTY_PAGE_SIZE, required = false)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setPageSize(@javax.annotation.Nullable Integer pageSize) {
    this.pageSize = pageSize;
  }

  @Override
  public String toString() {
    try {
      return OBJECT_MAPPER.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(
          "Failed to serialize GetListSchema to JSON for Superset q param", e);
    }
  }
}
