package main.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import main.api.request.PostProfileRequest;
import main.api.request.PostRequest;
import main.api.response.CalendarResponse;
import main.api.response.PostImageErrorResponse;
import main.api.response.PostImageResponse;
import main.api.response.PostResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterErrorResponse;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.api.response.dto.PostDTO;
import main.api.response.dto.TagDTO;
import main.api.response.projections.IDateCommentCount;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.User;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import main.utils.MappingUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final Tag2PostRepository tag2PostRepository;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public PostService(UserRepository userRepository, PostRepository postRepository,
      TagRepository tagRepository, Tag2PostRepository tag2PostRepository,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.tagRepository = tagRepository;
    this.tag2PostRepository = tag2PostRepository;
    this.authenticationManager = authenticationManager;
  }

  public TagResponse getTags(String query) {
    TagResponse response = new TagResponse();
    List<TagDTO> responseTags = new ArrayList<>();
    response.setTags(responseTags);
    List<Post> livePosts = postRepository.findAllLivePosts();

    for (Post post : livePosts){
      for (Tag tag : post.getTags()){
        if (tag.getName().contains(query)) {
          TagDTO current = new TagDTO(tag.getName(), 0.0d);
          for (TagDTO tagDTO : responseTags) {
            if (current.getName().equals(tagDTO.getName())) {
              current = tagDTO;
              break;
            }
          }
          if (current.getWeight() == 0.0d) {
            responseTags.add(current);
          }
          current.setWeight(current.getWeight() + 1);
        }
      }
    }

    TagDTO mostPopular = new TagDTO("place_holder", 0.0d);
    for (TagDTO tagDTO : responseTags){
      if (Double.compare(tagDTO.getWeight(), mostPopular.getWeight())>=0){
        mostPopular = tagDTO;
      }
    }
    if (responseTags.size()==0){
      return response;
    }
    double k = 1.0d / (mostPopular.getWeight() / livePosts.size());

    for (TagDTO tagDTO : responseTags){
      tagDTO.setWeight(k * (tagDTO.getWeight() / livePosts.size()));
    }

    response.setTags(responseTags);

    return response;
  }

  public PostResponse getPosts(int offset, int limit, String mode) {
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    Page<Post> postsPage = postRepository.findAllRecentPosts(pageRequest);
    switch (mode) {
      case "popular":
        postsPage = postRepository.findAllPostsByCommentCount(pageRequest);
        break;
      case "best":
        postsPage = postRepository.findAllPostsByLikeCount(pageRequest);;
        break;
      case "early":
        postsPage = postRepository.findAllOldPosts(pageRequest);
        break;
    }

    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  public PostResponse getPostsByQuery(int offset, int limit, String query){
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    Page<Post> postsPage = postRepository.findAllPostsContaining(query, pageRequest);
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  public CalendarResponse getCalendar(int year){
    CalendarResponse response = new CalendarResponse();
    response.setYears(postRepository.findAllActiveYears());
    List<IDateCommentCount> dateList = postRepository.findAllDatesWithPosts(year);
    for (IDateCommentCount line : dateList){
      response.getPosts().put(line.getCommentDate().toString(), line.getCommentCount());
    }
    return response;
  }

  public PostResponse getPostsByDate(int offset, int limit, Date date){
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    java.util.Date startDate = new java.util.Date(date.getTime() + ZoneOffset.systemDefault().getRules().getOffset(
        Instant.now()).getTotalSeconds()* 1000L);
    java.util.Date endDate = new java.util.Date(startDate.getTime() + 86400000);
    Page<Post> postsPage = postRepository.findAllByDate(startDate, endDate, pageRequest);
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  public PostResponse getPostsByTag(int offset, int limit, String tag){
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    Page<Post> postsPage = postRepository.findAllByTagName(tag, pageRequest);
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  public List<SinglePostResponse> getPostById(Authentication authentication, int id){
    List<SinglePostResponse> response = new ArrayList<>();
    List<Post> posts = postRepository.findPostById(id);
    for (Post post : posts){
      response.add(MappingUtils.mapPostToSinglePostDTO(post));
      boolean viewsShouldBeAdded = true;
      if (!(authentication instanceof AnonymousAuthenticationToken)) {
        User user = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException(authentication.getName()));
        if ((user.getIsModerator()==1) || (user.getId() == post.getUserId())){
          viewsShouldBeAdded = false;
        }
      }
      if (viewsShouldBeAdded) {
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
      }
    }
    return response;
  }

  public RegisterResponse post(Principal principal, PostRequest request){
    RegisterResponse response = new RegisterResponse();
    HashMap<String, String> errors = new HashMap<>();
    if (request.getText().replaceAll("<.*?>", "").length()<51){
      errors.put("text", "Текст публикации слишком короткий");
    }
    if (request.getTitle().length()<4){
      errors.put("title", "Заголовок слишком короткий");
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    Post post = new Post();
    post.setUser(user);
    post.setIsActive(request.getActive());
    post.setModerationStatus(ModerationStatus.NEW);
    post.setTitle(request.getTitle());
    if (request.getTimestamp()<System.currentTimeMillis()){
      post.setTime(new java.util.Date(System.currentTimeMillis()));
    } else {
      post.setTime(new java.util.Date(request.getTimestamp()));
    }
    post.setText(request.getText());
    post.setViewCount(0);
    postRepository.save(post);

    String[] tags = request.getTags();
    for (String s : tags) {
      Tag2Post tag2Post = new Tag2Post();
      tag2Post.setPost(post);
      Tag tag = tagRepository.findByName(s).orElse(new Tag());
      if (tag.getName() == null) {
        tag.setName(s);
        tag = tagRepository.save(tag);
      }
      tag2Post.setTag(tag);
      tag2PostRepository.save(tag2Post);
    }

    response.setResult(true);
    return response;
  }

  public PostImageResponse postImage(MultipartFile file, boolean cut){

    HashMap<String, String> errors = new HashMap<>();
    if (file.getSize() > 1000000){
      errors.put("image", "Размер файла превышает допустимый размер");
    }
    if ((!Objects.requireNonNull(file.getContentType()).contains("png")) && (!Objects.requireNonNull(file.getContentType()).contains("jpg"))
        && (!Objects.requireNonNull(file.getContentType()).contains("jpeg"))){
      errors.put("type", "Допустимы только расширения .png и .jpg/.jpeg");
    }
    if (errors.size()>0){
      PostImageErrorResponse errorResponse = new PostImageErrorResponse();
      errorResponse.setResult(false);
      errorResponse.setErrors(errors);
      return errorResponse;
    }
    String generatedString = RandomStringUtils.randomAlphanumeric(11);
    String pt1 = generatedString.substring(0,2);
    String pt2 = generatedString.substring(2,4);
    String pt3 = generatedString.substring(4,6);
    String fileName = generatedString.substring(6,11);
    if (file.getContentType().contains("png")){
      fileName += ".png";
    } else {
      fileName += ".jpg";
    }
    String home = System.getProperty("user.dir");
    String responseString = "\\upload\\" + pt1 + "\\" + pt2 + "\\" + pt3;
    String destinationDirectory = home + responseString;
    String filePath = destinationDirectory + "\\" + fileName;
    File destination = new File(destinationDirectory);
    destination.mkdirs();
    Path path = Paths.get(filePath);
    try{
      if (!cut) {
        try (InputStream inputStream = file.getInputStream()) {
          Files.copy(inputStream, path,
              StandardCopyOption.REPLACE_EXISTING);
        }
      } else{
        File tmpFile = new File(destinationDirectory + "/tmp" + fileName);
        File newFile = new File(filePath);
        file.transferTo(tmpFile);
        BufferedImage image = ImageIO.read(tmpFile);
        BufferedImage newImage = new BufferedImage(
            36,36, BufferedImage.TYPE_INT_RGB
        );

        int widthStep = image.getWidth() / 36;
        int heightStep = image.getHeight() / 36;
        for (int x = 0; x < 36; x++) {
          for (int y = 0; y < 36; y++) {
            int rgb = image.getRGB(x * widthStep, y * heightStep);
            newImage.setRGB(x, y, rgb);
          }
        }
        ImageIO.write(newImage, fileName.substring(fileName.length()-3), newFile);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    PostImageResponse response = new PostImageResponse();
//    responseString = "\\resources" + responseString;
    responseString += "\\" + fileName;
    responseString = responseString.replaceAll("\\\\", "/");
    response.setPath(responseString);

    return response;
  }

  public RegisterResponse changePost(Principal principal, PostRequest request, int id){
    RegisterResponse response = new RegisterResponse();
    HashMap<String, String> errors = new HashMap<>();
    List<Post> postList = postRepository.findPostById(id);
    if (request.getText().replaceAll("<.*?>", "").length()<51){
      errors.put("title", "Заголовок слишком короткий");
    }
    if (request.getTitle().length()<4){
      errors.put("text", "Текст публикации слишком короткий");
    }
    if (postList.size()==0){
      errors.put("id", "Этого поста больше не существует в базе данных");
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    Post post = postList.get(0);
    post.setIsActive(request.getActive());
    if (user.getIsModerator()==-1){
      post.setModerationStatus(ModerationStatus.NEW);
    }
    post.setTitle(request.getTitle());
    if (request.getTimestamp()<System.currentTimeMillis()){
      post.setTime(new java.util.Date(System.currentTimeMillis()));
    } else {
      post.setTime(new java.util.Date(request.getTimestamp()));
    }
    post.setText(request.getText());
    postRepository.save(post);

    String[] tags = request.getTags();
    for (String s : tags) {
      Tag2Post tag2Post = new Tag2Post();
      tag2Post.setPost(post);
      Tag tag = tagRepository.findByName(s).orElse(new Tag());
      if (tag.getName() == null) {
        tag.setName(s);
        tag = tagRepository.save(tag);
      }
      tag2Post.setTag(tag);
      tag2PostRepository.save(tag2Post);
    }
    response.setResult(true);
    return response;
  }

  public PostResponse getModerationPosts(Principal principal, int offset, int limit, String status){
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    Pageable pageRequest = PageRequest.of((offset / limit), limit);
    Page<Post> postsPage = postRepository.findAllNewPosts(pageRequest);
    switch (status) {
      case "declined":
        postsPage = postRepository.findAllDeclinedPostsByModId(user.getId(), pageRequest);
        break;
      case "accepted":
        postsPage = postRepository.findAllAcceptedPostsByModId(user.getId(), pageRequest);
        break;
    }

    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getTotalElements());
    response.setPosts(responsePosts);
    return response;
  }

  public RegisterResponse postModeration(Principal principal, int postId, String decision){
    RegisterResponse response = new RegisterResponse();
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    if (user.getIsModerator()!=1){
      response.setResult(false);
      return response;
    }

    List<Post> postList = postRepository.findPostById(postId);
    Post post = postList.get(0);

    switch (decision) {
      case "decline":
        post.setModerationStatus(ModerationStatus.DECLINED);
        break;
      case "accept":
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        break;
    }
    post.setModerator(user);
    postRepository.save(post);
    response.setResult(true);
    return response;
  }

  public RegisterResponse postMyProfile(Principal principal, String email, String password,
      String name, Integer removePhoto, MultipartFile photo){
    RegisterResponse response = new RegisterResponse();
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    HashMap<String, String> errors = new HashMap<>();

    if ((password!=null) && (password.length()<6)){
      errors.put("password", "Пароль короче 6-ти символов");
    }
    if (name!=null){
      List<Integer> userList = userRepository.findAllByName(name);
      if ((name.length()<2) || ((userList.size()>0) && (userList.get(0) != user.getId()))){
        errors.put("name", "Имя указано неверно");
      }
    }
    if (email!=null){
      List<Integer> userList = userRepository.findAllByEmail(email);
      if ((userList.size()>0) && (userList.get(0) != user.getId())){
        errors.put("email", "Этот email уже зарегистрирован");
      }
    }
    if (photo!=null){
      if (photo.getSize() > 1000000){
        errors.put("photo", "Превышен допустимый размер фотографии");
      } else {
        if ((!Objects.requireNonNull(photo.getContentType()).contains("png")) && (!Objects.requireNonNull(photo.getContentType()).contains("jpg"))
            && (!Objects.requireNonNull(photo.getContentType()).contains("jpeg"))) {
          errors.put("photo", "Допустимы только расширения .png и .jpg/.jpeg");
        }
      }
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    if (removePhoto!=null){
      if (user.getPhoto()!=null){
        File oldPhoto = new File(user.getPhoto());
        oldPhoto.delete();
        user.setPhoto(null);
      }

      if (removePhoto==0){
        user.setPhoto(postImage(photo, true).getPath());
      }
    }

    if (email!=null){
      user.setEmail(email);
    }
    if (name!=null){
      user.setName(name);
    }
    if (password!=null){
      PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      password = (encoder.encode(password)).substring(8);
      user.setPassword(password);
    }
    userRepository.save(user);
    response.setResult(true);
    return response;
  }

  public RegisterResponse postMyProfileNoPic(Principal principal, String name,
      String email, String password, Integer removePhoto){
    RegisterResponse response = new RegisterResponse();
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    HashMap<String, String> errors = new HashMap<>();

    if ((password!=null) && (password.length()<6)){
      errors.put("password", "Пароль короче 6-ти символов");
    }
    if (name!=null){
      List<Integer> userList = userRepository.findAllByName(name);
      if ((name.length()<2) || ((userList.size()>0) && (userList.get(0) != user.getId()))){
        errors.put("name", "Имя указано неверно");
      }
    }
    if (email!=null){
      List<Integer> userList = userRepository.findAllByEmail(email);
      if ((userList.size()>0) && (userList.get(0) != user.getId())){
        errors.put("email", "Этот email уже зарегистрирован");
      }
    }
    if (errors.size()>0){
      RegisterErrorResponse responseWithErrors = new RegisterErrorResponse();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    if (removePhoto!=null){
      if (user.getPhoto()!=null){
        File oldPhoto = new File(user.getPhoto());
        oldPhoto.delete();
        user.setPhoto(null);
      }
    }
    if (email!=null){
      user.setEmail(email);
    }
    if (name!=null){
      user.setName(name);
    }
    if (password!=null){
      PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
      password = (encoder.encode(password)).substring(8);
      user.setPassword(password);
    }
    userRepository.save(user);
    response.setResult(true);
    return response;
  }
}
