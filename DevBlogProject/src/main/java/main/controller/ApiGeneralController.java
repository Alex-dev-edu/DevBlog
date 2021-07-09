package main.controller;

import java.security.Principal;
import main.api.request.SettingsRequest;
import main.api.response.CaptchaResponse;
import main.api.response.CommentResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.service.CaptchaService;
import main.service.PostCommentService;
import main.service.SettingsService;
import main.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final SettingsService settingsService;
  private final CaptchaService captchaService;
  private final StatisticsService statisticsService;
  private final PostCommentService commentService;

  public ApiGeneralController(InitResponse initResponse,
      SettingsService settingsService,
      CaptchaService captchaService, StatisticsService statisticsService,
      PostCommentService commentService) {
    this.initResponse = initResponse;
    this.settingsService = settingsService;
    this.captchaService = captchaService;
    this.statisticsService = statisticsService;
    this.commentService = commentService;
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

  @PostMapping("/comment")
  public ResponseEntity<CommentResponse> postComment(Principal principal, @RequestParam String parent_id,
      @RequestParam int post_id, @RequestParam String text){
    return commentService.postComment(principal, parent_id, post_id, text);
  }

  @PutMapping("/settings")
  @PreAuthorize("hasAuthority('user:moderate')")
  public void putSettings(@RequestBody SettingsRequest request){
    settingsService.setGlobalSettings(request.isMULTIUSER_MODE(), request.isPOST_PREMODERATION(), request.isSTATISTICS_IS_PUBLIC());
  }
}
