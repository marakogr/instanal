package ru.marakogr.instanal.integration.superset.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetListResponse<T> {
  private int page;
  private int pageSize;
  private List<Integer> ids;
  private int count;
  private List<T> result;
}
