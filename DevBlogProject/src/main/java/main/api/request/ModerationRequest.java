package main.api.request;

import lombok.Data;

@Data
public class ModerationRequest {
  private int post_id;
  private String decision;
}
