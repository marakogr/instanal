package ru.marakogr.instanal.integration.superset.model;

import java.util.Collection;
import java.util.Map;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

  private final int statusCode;
  private final Map<String, Collection<String>> headers;
  private final T data;

  /**
   * @param statusCode The status code of HTTP response
   * @param headers The headers of HTTP response
   */
  public ApiResponse(int statusCode, Map<String, Collection<String>> headers) {
    this(statusCode, headers, null);
  }

  /**
   * @param statusCode The status code of HTTP response
   * @param headers The headers of HTTP response
   * @param data The object deserialized from response bod
   */
  public ApiResponse(int statusCode, Map<String, Collection<String>> headers, T data) {
    this.statusCode = statusCode;
    this.headers = headers;
    this.data = data;
  }
}
