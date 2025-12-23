package ru.marakogr.instanal.integration.superset.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Objects;

/** GetListSchemaFiltersInner */
@JsonPropertyOrder({
  GetListSchemaFiltersInner.JSON_PROPERTY_COL,
  GetListSchemaFiltersInner.JSON_PROPERTY_OPR,
  GetListSchemaFiltersInner.JSON_PROPERTY_VALUE
})
@JsonTypeName("get_list_schema_filters_inner")
public class GetListSchemaFiltersInner {
  public static final String JSON_PROPERTY_COL = "col";
  @javax.annotation.Nonnull private String col;

  public static final String JSON_PROPERTY_OPR = "opr";
  @javax.annotation.Nonnull private String opr;

  public static final String JSON_PROPERTY_VALUE = "value";
  @javax.annotation.Nonnull private String value;

  public GetListSchemaFiltersInner() {}

  public GetListSchemaFiltersInner col(@javax.annotation.Nonnull String col) {

    this.col = col;
    return this;
  }

  /**
   * Get col
   *
   * @return col
   */
  @javax.annotation.Nonnull
  @JsonProperty(value = JSON_PROPERTY_COL, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getCol() {
    return col;
  }

  @JsonProperty(value = JSON_PROPERTY_COL, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setCol(@javax.annotation.Nonnull String col) {
    this.col = col;
  }

  public GetListSchemaFiltersInner opr(@javax.annotation.Nonnull String opr) {

    this.opr = opr;
    return this;
  }

  /**
   * Get opr
   *
   * @return opr
   */
  @javax.annotation.Nonnull
  @JsonProperty(value = JSON_PROPERTY_OPR, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getOpr() {
    return opr;
  }

  @JsonProperty(value = JSON_PROPERTY_OPR, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setOpr(@javax.annotation.Nonnull String opr) {
    this.opr = opr;
  }

  public GetListSchemaFiltersInner value(@javax.annotation.Nonnull String value) {

    this.value = value;
    return this;
  }

  /**
   * Get value
   *
   * @return value
   */
  @javax.annotation.Nonnull
  @JsonProperty(value = JSON_PROPERTY_VALUE, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public String getValue() {
    return value;
  }

  @JsonProperty(value = JSON_PROPERTY_VALUE, required = true)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setValue(@javax.annotation.Nonnull String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetListSchemaFiltersInner getListSchemaFiltersInner = (GetListSchemaFiltersInner) o;
    return Objects.equals(this.col, getListSchemaFiltersInner.col)
        && Objects.equals(this.opr, getListSchemaFiltersInner.opr)
        && Objects.equals(this.value, getListSchemaFiltersInner.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(col, opr, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetListSchemaFiltersInner {\n");
    sb.append("    col: ").append(toIndentedString(col)).append("\n");
    sb.append("    opr: ").append(toIndentedString(opr)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
