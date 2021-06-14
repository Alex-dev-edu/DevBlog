package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import main.api.response.dto.AuthCheckUserDTO;

@Data
@JsonInclude(Include.NON_NULL)
public class AuthCheckResponse {

  private boolean result;

  private AuthCheckUserDTO user;
}
