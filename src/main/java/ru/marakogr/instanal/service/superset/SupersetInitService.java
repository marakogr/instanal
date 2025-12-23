package ru.marakogr.instanal.service.superset;

import feign.FeignException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.integration.superset.api.DatabaseApi;
import ru.marakogr.instanal.integration.superset.api.DatasetApi;
import ru.marakogr.instanal.integration.superset.model.DatasetPostRequest;

@Slf4j
@Service
public class SupersetInitService {

  private final DatasetApi datasetApi;
  private final DatabaseApi databaseApi;

  public SupersetInitService(SupersetService supersetService) {
    this.databaseApi = supersetService.getApi(DatabaseApi.class);
    this.datasetApi = supersetService.getApi(DatasetApi.class);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void initCommonDatasets() {
    try {
      var response = datasetApi.apiV1DatasetGetList(null);
      if (response.getData().getResult() == null || response.getData().getResult().isEmpty()) {
        createDatasets();
      }
    } catch (FeignException.NotFound e) {
      createDatasets();
    }
  }

  private void createDatasets() {
    createDataset("messages", "Main message table");
    createDataset("average_reels_per_day", "Max and Avg Reels aggregation");
  }

  private void createDataset(String tableName, String description) {
    try {
      var datasetPostRequest =
          DatasetPostRequest.builder()
              .database(databaseApi.apiV1DatabaseGet(null).getData().getResult().getFirst().getId())
              .schema("public")
              .tableName(tableName)
              .owners(List.of(1L))
              .build();
      datasetApi.apiV1DatasetPost(datasetPostRequest);
    } catch (Exception exception) {
      log.error("Unable to create default datasets", exception);
    }
  }
}
