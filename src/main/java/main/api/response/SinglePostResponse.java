package main.api.response;

import java.util.List;
import lombok.Data;
import main.api.response.dto.CommentDTO;
import main.api.response.dto.PostUserDTO;

@Data
public class SinglePostResponse {
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
