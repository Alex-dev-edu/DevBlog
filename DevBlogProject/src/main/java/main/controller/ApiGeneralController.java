package main.controller;

import main.api.response.CaptchaResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.service.CaptchaService;
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
  private final CaptchaService captchaService;

  public ApiGeneralController(InitResponse initResponse,
      SettingsService settingsService, PostService postService,
      CaptchaService captchaService) {
    this.initResponse = initResponse;
    this.settingsService = settingsService;
    this.captchaService = captchaService;
  }

  @GetMapping("/settings")
  private SettingsResponse settings() {
    return settingsService.getGlobalSettings();
  }

  @GetMapping("/init")
  private InitResponse init() {
    return initResponse;
  }

  @GetMapping("/auth/captcha")
  private CaptchaResponse captcha(){
    return captchaService.getAndAddCaptcha();
  }
}
