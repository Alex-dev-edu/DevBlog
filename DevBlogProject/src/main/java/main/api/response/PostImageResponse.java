package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class PostImageResponse {
  private String path;
}
