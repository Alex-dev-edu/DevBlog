package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class PostCommentRequest {
  @JsonProperty("parent_id")
  private Integer parentId;
  @JsonProperty("post_id")
  private Integer postId;
  private String text;
}
