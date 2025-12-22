package ru.marakogr.instanal.integration.superset.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class IdArrayWrapper {
  private List<IdWrapper> result;
}
