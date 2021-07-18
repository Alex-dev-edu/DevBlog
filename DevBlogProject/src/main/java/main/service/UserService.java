package main.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.AuthCheckResponse;
import main.api.response.PostResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterErrorResponse;
import main.api.response.dto.AuthCheckUserDTO;
import main.api.response.dto.PostDTO;
import main.model.Post;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.utils.MappingUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  private final AuthenticationManager authenticationManager;
  private final JavaMailSender mailSender;

  @Autowired
  public UserService(UserRepository userRepository,
      PostRepository postRepository, AuthenticationManager authenticationManager,
      JavaMailSender mailSender) {
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.authenticationManager = authenticationManager;
    this.mailSender = mailSender;
  }

  public RegisterResponse register(RegisterRequest request){
    HashMap<String, String> errors = new HashMap<>();
    if (userRepository.findAllByEmail(request.getEmail()).size()!=0){
      errors.put("email", "Такой email уже зарегистрирован");
    }
    if ((request.getName()==null)||(request.getName().length()<1)||(userRepository.findAllByName(request.getName()).size()!=0)){
      errors.put("name", "Имя указано неверно");
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }
    User user = new User();
    user.setName(request.getName());
    user.setPassword(request.getPassword());
    user.setEmail(request.getEmail());
    user.setRegTime(new Date(System.currentTimeMillis()));
    user.setIsModerator(-1);
    userRepository.save(user);
    RegisterResponse response = new RegisterResponse();
    response.setResult(true);
    return response;
  }

  public AuthCheckResponse login(LoginRequest request){
    Authentication auth = authenticationManager
        .authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(auth);
    org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

    return getLoginResponse(user.getUsername());
  }

  public RegisterResponse logout(){
    SecurityContextHolder.clearContext();
    RegisterResponse response = new RegisterResponse();
    response.setResult(true);
    return response;
  }

  public AuthCheckResponse check(Principal principal){
    return getLoginResponse(principal.getName());
  }

  public PostResponse getMyPosts(Principal principal, int limit, int offset, String status){
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    Page<Post> postsPage = postRepository.findAllPublishedPostsById(user.getId(), pageRequest);
    switch (status){
      case "inactive":
        postsPage = postRepository.findAllInactivePostsById(user.getId(), pageRequest);
        break;
      case "pending":
        postsPage = postRepository.findAllPendingPostsById(user.getId(), pageRequest);
        break;
      case "declined":
        postsPage = postRepository.findAllDeclinedPostsById(user.getId(), pageRequest);
    }

    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  private AuthCheckResponse getLoginResponse(String email){
    User currentUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));

    AuthCheckUserDTO userDto = new AuthCheckUserDTO();
    userDto.setEmail(currentUser.getEmail());
    userDto.setModeration(currentUser.getIsModerator() == 1);
    userDto.setId(currentUser.getId());
    userDto.setName(currentUser.getName());
    userDto.setSettings(true);
    if (currentUser.getIsModerator() == 1){
      Pageable pageRequest = PageRequest.of(0, 10);
      Page<Post> postsPage = postRepository.findAllNewPosts(pageRequest);
      userDto.setModerationCount(postsPage.getTotalElements());
    }

    AuthCheckResponse response = new AuthCheckResponse();
    response.setUser(userDto);
    response.setResult(true);
    return response;
  }

  public RegisterResponse restoreGetCode(String currUrl, String email){
    RegisterResponse response = new RegisterResponse();
    User user = userRepository.findByEmail(email)
        .orElse(new User());

    if (user.getRegTime()==null){
      return response;
    }

    String generatedString = RandomStringUtils.randomAlphanumeric(20);
    user.setCode(generatedString);
    userRepository.save(user);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("noreply@devblogdiploma.com");
    message.setTo(email);
    message.setSubject("Restore Password");
    String newURL = currUrl.substring(0, currUrl.length() - 17) + "/login/change-password/" + generatedString;
    System.out.println(newURL);
    message.setText("This link will restore your password at devblog: " + newURL);
    mailSender.send(message);
    response.setResult(true);
    return response;
  }

  public RegisterResponse validateCode(String code, String newPassword){
    RegisterResponse response = new RegisterResponse();
    List<User> userList = userRepository.findAllByCode(code);

    HashMap<String, String> errors = new HashMap<>();
    if (userList.size()==0){
      errors.put("code", "Ссылка для восстановления пароля устарела. <a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>");
    }
    if (newPassword.length()<6){
      errors.put("password", "Пароль короче 6ти символов");
    }
    if (userList.size()>1){
      errors.put("security", "Сбой в системе кодов верификации. <a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>");
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    User user = userList.get(0);
    user.setPassword(newPassword);
    userRepository.save(user);
    response.setResult(true);
    return response;
  }
}
