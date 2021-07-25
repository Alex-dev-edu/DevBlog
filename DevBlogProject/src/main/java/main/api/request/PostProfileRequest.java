package main.api.request;

import lombok.Data;

@Data
public class PostProfileRequest {
  private String name;
  private String email;
  private String password;
  private Integer removePhoto;
}
