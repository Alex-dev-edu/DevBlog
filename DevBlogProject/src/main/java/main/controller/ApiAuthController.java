package main.controller;

import main.api.request.RegisterRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterResponseWithErrors;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiAuthController {

  private final UserService userService;
  private final CaptchaService captchaService;

  public ApiAuthController(UserService userService, CaptchaService captchaService) {
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @PostMapping("/auth/register")
  private RegisterResponse authRegister(@RequestBody RegisterRequest request){
    if (!captchaService.validateCaptcha(request.getCaptcha(), request.getCaptcha_secret())){
      RegisterResponseWithErrors responseWithError = new RegisterResponseWithErrors();
      responseWithError.setResult(false);
      responseWithError.getErrors().put("captcha", "Код с картинки введен неверно");
      return responseWithError;
    }
    return userService.register(request);
  }

  @GetMapping("/auth/check")
  private AuthCheckResponse authCheck() {
    return new AuthCheckResponse();
  }
}
