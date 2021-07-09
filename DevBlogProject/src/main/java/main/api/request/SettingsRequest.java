package main.api.request;

import lombok.Data;

@Data
public class SettingsRequest {
  private boolean MULTIUSER_MODE;
  private boolean POST_PREMODERATION;
  private boolean STATISTICS_IS_PUBLIC;
}
