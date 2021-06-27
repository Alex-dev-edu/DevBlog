package main.api.response;

import java.util.HashMap;
import lombok.Data;

@Data
public class RegisterResponseWithErrors extends  RegisterResponse{
   HashMap<String, String> errors = new HashMap<>();
}
