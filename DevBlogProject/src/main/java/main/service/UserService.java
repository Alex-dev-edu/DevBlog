package main.service;

import java.util.Date;
import java.util.HashMap;
import main.api.request.RegisterRequest;
import main.api.response.RegisterResponse;
import main.api.response.RegisterResponseWithErrors;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public RegisterResponse register(RegisterRequest request){
    HashMap<String, String> errors = new HashMap<>();
    if (userRepository.findAllByEmail(request.getE_mail()).size()!=0){
      errors.put("email", "Такой email уже зарегистрирован");
    }
    if ((request.getName()==null)||(request.getName().length()<1)||(userRepository.findAllByName(request.getName()).size()!=0)){
      errors.put("name", "Имя указано неверно");
    }
    if (request.getPassword().length()<6){
      errors.put("name", "Пароль короче 6-ти символов");
    }
    if (errors.size()>0){
      RegisterResponseWithErrors responseWithErrors = new RegisterResponseWithErrors();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }
    User user = new User();
    user.setName(request.getName());
    user.setPassword(request.getPassword());
    user.setEmail(request.getE_mail());
    user.setRegTime(new Date(System.currentTimeMillis()));
    user.setIsModerator(-1);
    userRepository.save(user);
    RegisterResponse response = new RegisterResponse();
    response.setResult(true);
    return response;
  }

}
