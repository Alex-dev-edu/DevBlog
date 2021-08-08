package main.api.response;

import java.util.HashMap;
import lombok.Data;

@Data
public class RegisterErrorResponse extends  RegisterResponse{
   private HashMap<String, String> errors = new HashMap<>();
}
