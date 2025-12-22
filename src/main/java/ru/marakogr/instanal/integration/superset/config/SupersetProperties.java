package ru.marakogr.instanal.integration.superset.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "superset")
@Component
@Data
public class SupersetProperties {
  private String url = "http://localhost:8088";
  private String publicUrl = "http://localhost:8088";
  private String username = "admin";
  private String password = "admin";
}
