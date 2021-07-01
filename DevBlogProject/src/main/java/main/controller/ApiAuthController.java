package main.controller;

import java.security.Principal;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterResponseWithErrors;
import main.api.response.dto.AuthCheckUserDTO;
import main.model.User;
import main.repository.UserRepository;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  public ApiAuthController(UserService userService, CaptchaService captchaService,
      AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @PostMapping("/register")
  public RegisterResponse authRegister(@RequestBody RegisterRequest request){
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
  public ResponseEntity<AuthCheckResponse> login(@RequestBody LoginRequest loginRequest){
    return ResponseEntity.ok(userService.login(loginRequest));
  }

  @GetMapping("/check")
  public AuthCheckResponse authCheck(Principal principal) {
//    AuthCheckResponse response = new AuthCheckResponse();
//    response.setResult(true);
//    AuthCheckUserDTO userDTO = new AuthCheckUserDTO();
//    userDTO.setEmail("vitya.pupkin@mail.ru");
//    userDTO.setModeration(false);
//    userDTO.setId(3);
//    userDTO.setName("Vitek");
//    response.setUser(userDTO);
//    return response;
    if (principal == null){
      return new AuthCheckResponse();
    }
    return userService.check(principal);
  }
}
