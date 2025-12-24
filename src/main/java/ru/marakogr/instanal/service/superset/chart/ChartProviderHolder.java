package ru.marakogr.instanal.service.superset.chart;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import ru.marakogr.instanal.chat.Constants;
import ru.marakogr.instanal.integration.superset.SupersetService;
import ru.marakogr.instanal.service.superset.chart.impl.ConfigChartProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChartProviderHolder {
  @Getter private final List<ConfigChartProvider> providers = new ArrayList<>();
  private final SupersetService supersetService;

  @PostConstruct
  public void init() {
    try {
      var resources =
          new PathMatchingResourcePatternResolver().getResources("classpath:config/charts/*.json");
      for (var resource : resources) {
        try (InputStream is = resource.getInputStream()) {
          var config = Constants.OBJECT_MAPPER.readValue(is, ConfigChartProvider.ChartConfig.class);
          log.info("loaded chart provider configuration: {}", config.description());
          var provider = new ConfigChartProvider(supersetService, config);
          providers.add(provider);
        } catch (IOException e) {
          throw new RuntimeException(
              "Failed to load chart config from " + resource.getFilename(), e);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to scan chart config directory", e);
    }
  }
}
