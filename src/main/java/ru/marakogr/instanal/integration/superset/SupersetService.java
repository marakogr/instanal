package ru.marakogr.instanal.integration.superset;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.integration.superset.api.ChartApi;
import ru.marakogr.instanal.integration.superset.api.DashboardsApi;
import ru.marakogr.instanal.integration.superset.api.DatasetApi;
import ru.marakogr.instanal.integration.superset.client.ApiClient;

@Slf4j
@Service
public class SupersetService {
  private final Map<Class<? extends ApiClient.Api>, ApiClient.Api> classApiMap = new HashMap<>();
  private final ApiClient client;

  public SupersetService(@Qualifier("supersetApiClient") ApiClient client) {
    this.client = client;
  }

  @SuppressWarnings("unchecked")
  public <T extends ApiClient.Api> T getApi(Class<T> aClass) {
    return (T) classApiMap.computeIfAbsent(aClass, client::buildClient);
  }

  public void rollbackSuperset(Map<String, Set<String>> supersetToId) {
    try {
      supersetToId.forEach(
          (type, ids) -> {
            switch (type) {
              case "dataset":
                {
                  ids.forEach(getApi(DatasetApi.class)::apiV1DatasetDelete);
                  break;
                }
              case "chart":
                {
                  ids.forEach(getApi(ChartApi.class)::apiV1ChartDelete);
                  break;
                }
              case "dashboard":
                {
                  ids.forEach(getApi(DashboardsApi.class)::apiV1DashboardDelete);
                  break;
                }
            }
          });
    } catch (Exception rollbackException) {
      log.warn("unable to rollback superset entities", rollbackException);
      throw rollbackException;
    }
  }
}
