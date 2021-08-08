package main.api.response.dto;

import lombok.Data;

@Data
public class CommentDTO {
  private int id;

  private long timestamp;

  private String text;

  CommentUserDTO user = new CommentUserDTO();
}
