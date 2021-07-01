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
import main.api.response.RegisterResponseWithErrors;
import main.api.response.dto.AuthCheckUserDTO;
import main.api.response.dto.PostDTO;
import main.model.Post;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  @Autowired
  public UserService(UserRepository userRepository,
      PostRepository postRepository, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.authenticationManager = authenticationManager;
  }

  public RegisterResponse register(RegisterRequest request){
    HashMap<String, String> errors = new HashMap<>();
    if (userRepository.findAllByEmail(request.getEmail()).size()!=0){
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
      case "pending":
        postsPage = postRepository.findAllPendingPostsById(user.getId(), pageRequest);
      case "declined":
        postsPage = postRepository.findAllDeclinedPostsById(user.getId(), pageRequest);
    }

    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getNumber());
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

    AuthCheckResponse response = new AuthCheckResponse();
    response.setUser(userDto);
    response.setResult(true);
    return response;
  }
}
