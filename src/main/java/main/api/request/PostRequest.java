package main.api.request;

import lombok.Data;

@Data
public class PostRequest {

  private long timestamp;
  private int active;
  private String title;
  private String[] tags;
  private String text;
}
