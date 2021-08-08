package main.api.response;

import java.util.HashMap;
import lombok.Data;

@Data
public class CommentErrorResponse extends CommentResponse{
  private boolean result;

  private HashMap<String, String> errors = new HashMap<>();
}
