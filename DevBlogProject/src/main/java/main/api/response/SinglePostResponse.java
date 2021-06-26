package main.api.response.dto;

import java.util.List;
import lombok.Data;

@Data
public class SinglePostDTO {
  private int id;

  private long timestamp;

  private boolean active;

  private PostUserDTO user = new PostUserDTO();

  private String title;

  private String text;

  private int likeCount;

  private int dislikeCount;

  private int viewCount;

  List<CommentDTO> comments;

  List<String> tags;
}
