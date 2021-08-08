package main.api.response;


import java.util.List;
import lombok.Data;
import main.api.response.dto.PostDTO;

@Data
public class PostResponse {

  private long count;

  private List<PostDTO> posts;
}
