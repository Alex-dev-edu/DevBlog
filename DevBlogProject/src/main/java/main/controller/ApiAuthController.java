package main.controller;

import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.LoginResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterResponseWithErrors;
import main.config.SecurityConfig;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

  private final UserService userService;
  private final CaptchaService captchaService;

  public ApiAuthController(UserService userService, CaptchaService captchaService) {
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @PostMapping("/register")
  private RegisterResponse authRegister(@RequestBody RegisterRequest request){
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    request.setPassword(encoder.encode(request.getPassword()));
    if (!captchaService.validateCaptcha(request.getCaptcha(), request.getCaptchaSecret())){
      RegisterResponseWithErrors responseWithError = new RegisterResponseWithErrors();
      responseWithError.setResult(false);
      responseWithError.getErrors().put("captcha", "Код с картинки введен неверно");
      return responseWithError;
    }
    return userService.register(request);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){

    return ResponseEntity.ok(new LoginResponse());
  }

  @GetMapping("/check")
  private AuthCheckResponse authCheck() {
    return new AuthCheckResponse();
  }
}
