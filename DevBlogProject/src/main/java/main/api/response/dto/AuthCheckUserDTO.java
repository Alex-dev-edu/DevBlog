package main.api.response.dto;

import lombok.Data;

@Data
public class AuthCheckUserDTO {
  private int id;

  private String name;

  private String photo;

  private String email;

  private boolean moderation;

  private long moderationCount;

  private boolean settings;
}
