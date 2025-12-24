package ru.marakogr.instanal.integration.superset.model;

import static ru.marakogr.instanal.chat.Constants.OBJECT_MAPPER;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.Builder;

@JsonPropertyOrder({"columns", "filters", "keys", "page", "page_size"})
@JsonTypeName("get_list_schema")
@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
@Builder(toBuilder = true)
public record GetListSchema(
    @JsonProperty("columns") List<String> columns,
    @JsonProperty("filters") List<GetListSchemaFiltersInner> filters,
    @JsonProperty("keys") List<KeysEnum> keys,
    @JsonProperty("page") Integer page,
    @JsonProperty("page_size") Integer pageSize) {

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
