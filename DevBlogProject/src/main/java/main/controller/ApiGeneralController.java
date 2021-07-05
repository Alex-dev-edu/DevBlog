package main.controller;

import java.security.Principal;
import main.api.response.CaptchaResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.service.CaptchaService;
import main.service.SettingsService;
import main.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final SettingsService settingsService;
  private final CaptchaService captchaService;
  private final StatisticsService statisticsService;

  public ApiGeneralController(InitResponse initResponse,
      SettingsService settingsService,
      CaptchaService captchaService, StatisticsService statisticsService) {
    this.initResponse = initResponse;
    this.settingsService = settingsService;
    this.captchaService = captchaService;
    this.statisticsService = statisticsService;
  }

  @GetMapping("/settings")
  public SettingsResponse settings() {
    return settingsService.getGlobalSettings();
  }

  @GetMapping("/init")
  public InitResponse init() {
    return initResponse;
  }

  @GetMapping("/auth/captcha")
  public CaptchaResponse captcha(){
    return captchaService.getAndAddCaptcha();
  }

  @GetMapping("/statistics/my")
  public StatisticsResponse statisticsMy(Principal principal){
    return statisticsService.statisticsMy(principal);
  }

  @GetMapping("/statistics/all")
  public ResponseEntity<StatisticsResponse> statisticsAll(Principal principal){
    return statisticsService.statisticsGlobal(principal);
  }
}
