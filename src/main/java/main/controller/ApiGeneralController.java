package main.controller;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import main.api.request.PostCommentRequest;
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
@RequiredArgsConstructor
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final SettingsService settingsService;
  private final CaptchaService captchaService;
  private final StatisticsService statisticsService;
  private final PostCommentService commentService;

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
  public ResponseEntity<CommentResponse> postComment(Principal principal, @RequestBody
      PostCommentRequest request){
    return commentService.postComment(principal, request.getParentId(), request.getPostId(),
        request.getText());
  }

  @PutMapping("/settings")
  @PreAuthorize("hasAuthority('user:moderate')")
  public void putSettings(@RequestBody SettingsRequest request){
    settingsService.setGlobalSettings(request.isMultiuserMode(), request.isPostPremoderation(), request.isStatisticsPublic());
  }
}
