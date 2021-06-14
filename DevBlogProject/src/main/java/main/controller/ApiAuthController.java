package main.controller;

import main.api.response.AuthCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiAuthController {

  @GetMapping("/auth/check")
  private AuthCheckResponse authCheck() {
    return new AuthCheckResponse();
  }
}
