package main.api.request;

import lombok.Data;

@Data
public class RestorePasswordRequest {
  private String email;
}
