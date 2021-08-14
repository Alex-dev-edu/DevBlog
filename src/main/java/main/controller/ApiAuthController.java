package main.controller;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestorePasswordRequest;
import main.api.request.RestorePasswordValidationRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterErrorResponse;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

  private final UserService userService;
  private final CaptchaService captchaService;

  @PostMapping("/register")
  public RegisterResponse authRegister(@RequestBody RegisterRequest request){
    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    request.setPassword((encoder.encode(request.getPassword())).substring(8));
    if (!captchaService.validateCaptcha(request.getCaptcha(), request.getCaptchaSecret())){
      RegisterErrorResponse responseWithError = new RegisterErrorResponse();
      responseWithError.setResult(false);
      responseWithError.getErrors().put("captcha", "Код с картинки введен неверно");
      return responseWithError;
    }
    return userService.register(request);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthCheckResponse> login(@RequestBody LoginRequest loginRequest){
    return ResponseEntity.ok(userService.login(loginRequest));
  }

  @GetMapping("/logout")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<RegisterResponse> logout(){
    return ResponseEntity.ok(userService.logout());
  }

  @GetMapping("/check")
  public AuthCheckResponse authCheck(Principal principal) {
    if (principal == null){
      return new AuthCheckResponse();
    }
    return userService.check(principal);
  }

  @PostMapping("/restore")
  public RegisterResponse restorePass(HttpServletRequest servletRequest, @RequestBody RestorePasswordRequest request){
    return userService.restoreGetCode(servletRequest.getRequestURL().toString(), request.getEmail());
  }

  @PostMapping("/password")
  public RegisterResponse postPassword(@RequestBody RestorePasswordValidationRequest request){
    if (!captchaService.validateCaptcha(request.getCaptcha(), request.getCaptchaSecret())){
      RegisterErrorResponse responseWithError = new RegisterErrorResponse();
      responseWithError.setResult(false);
      responseWithError.getErrors().put("captcha", "Код с картинки введен неверно");
      return responseWithError;
    }
    return userService.validateCode(request.getCode(), request.getPassword());
  }
}
