package main.api.response;

import java.util.List;
import lombok.Data;
import main.api.response.dto.TagDTO;

@Data
public class TagResponse {
  private List<TagDTO> tags;
}
