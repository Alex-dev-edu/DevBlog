package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.service.PostService;
import main.service.SettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final SettingsService settingsService;

  public ApiGeneralController(InitResponse initResponse,
      SettingsService settingsService, PostService postService) {
    this.initResponse = initResponse;
    this.settingsService = settingsService;
  }

  @GetMapping("/settings")
  private SettingsResponse settings() {
    return settingsService.getGlobalSettings();
  }

  @GetMapping("/init")
  private InitResponse init() {
    return initResponse;
  }

}
