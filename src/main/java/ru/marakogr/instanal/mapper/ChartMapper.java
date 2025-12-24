package ru.marakogr.instanal.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.marakogr.instanal.integration.superset.model.ChartPostRequest;
import ru.marakogr.instanal.integration.superset.model.ChartResponse;
import ru.marakogr.instanal.integration.superset.model.IdWrapper;

@Mapper(componentModel = "spring")
public interface ChartMapper {

  @Mapping(target = "dashboards", source = "dashboards", qualifiedByName = "idWrapperToIntList")
  @Mapping(target = "owners", source = "owners", qualifiedByName = "idWrapperToLongList")
  ChartPostRequest map(ChartResponse response);

  @Named("idWrapperToIntList")
  static List<Integer> idWrapperToIntList(List<IdWrapper> dashboards) {
    if (dashboards == null || dashboards.isEmpty()) {
      return Collections.emptyList();
    }
    return dashboards.stream().map(IdWrapper::getId).map(Integer::parseInt).toList();
  }

  @Named("idWrapperToLongList")
  static List<Long> idWrapperToLongList(List<IdWrapper> dashboards) {
    if (dashboards == null || dashboards.isEmpty()) {
      return Collections.emptyList();
    }
    return dashboards.stream().map(IdWrapper::getId).map(Long::parseLong).toList();
  }
}
