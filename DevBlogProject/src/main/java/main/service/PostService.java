package main.service;

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
import java.util.Comparator;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import main.api.request.PostRequest;
import main.api.response.CalendarResponse;
import main.api.response.PostImageErrorResponse;
import main.api.response.PostImageResponse;
import main.api.response.PostResponse;
import main.api.response.RegisterResponse;
import main.api.response.RegisterResponseWithErrors;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.api.response.dto.PostDTO;
import main.api.response.dto.TagDTO;
import main.api.response.projections.IDateCommentCount;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.Tag;
import main.model.Tag2Post;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import main.utils.MappingUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private Tag2PostRepository tag2PostRepository;

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
    System.out.println(startDate.getTime());
    System.out.println(endDate.getTime());
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

  public List<SinglePostResponse> getPostById(int id){
    List<SinglePostResponse> response = new ArrayList<>();
    List<Post> posts = postRepository.findPostById(id);
    for (Post post : posts){
      response.add(MappingUtils.mapPostToSinglePostDTO(post));
    }
    return response;
  }

  public RegisterResponse post(Principal principal, PostRequest request){
    RegisterResponse response = new RegisterResponse();
    HashMap<String, String> errors = new HashMap<>();
    if (request.getText().replaceAll("<.*?>", "").length()<51){
      errors.put("title", "Заголовок слишком короткий");
    }
    if (request.getTitle().length()<4){
      errors.put("text", "Текст публикации слишком короткий");
    }
    if (errors.size()>0){
      RegisterResponseWithErrors responseWithErrors = new RegisterResponseWithErrors();
      responseWithErrors.setErrors(errors);
      responseWithErrors.setResult(false);
      return responseWithErrors;
    }

    Post post = new Post();
    post.setUserId(post.getUserId());
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
      tag2Post.setPostId(post.getId());
      Tag tag = tagRepository.findByName(s).orElse(new Tag());
      if (tag.getName() == null) {
        tag.setName(s);
        tag = tagRepository.save(tag);
      }
      tag2Post.setTagId(tag.getId());
      tag2PostRepository.save(tag2Post);
    }
    response.setResult(true);
    return response;
  }

  public PostImageResponse postImage(MultipartFile file){

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
    if ((!Objects.requireNonNull(file.getContentType()).contains("png"))){
      fileName += ".png";
    } else {
      fileName += ".jpg";
    }
    String destinationDirectory = "/upload/" + pt1 + "/" + pt2 + "/" + pt3;
    String filePath = destinationDirectory + "/" + fileName;
    File destination = new File(destinationDirectory);
    destination.mkdirs();
    Path path = Paths.get(filePath);
    try{

      try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, path,
          StandardCopyOption.REPLACE_EXISTING);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    PostImageResponse response = new PostImageResponse();
    response.setPath(filePath);
    return response;
  }
}
