package main.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import main.api.response.CalendarResponse;
import main.api.response.PostResponse;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.api.response.dto.PostDTO;
import main.api.response.dto.TagDTO;
import main.api.response.projections.IDateCommentCount;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.TagRepository;
import main.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private TagRepository tagRepository;

  public TagResponse getTags(String query) {
    TagResponse response = new TagResponse();
    List<TagDTO> responseTags = new ArrayList<>();
    response.setTags(responseTags);
    List<Post> livePosts = new ArrayList<>();

    for (Post post : postRepository.findAll()) {
      if (post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && (post.getTime().getTime()
          <= System.currentTimeMillis())){
        livePosts.add(post);
      }
    }

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
    response.setCount(postsPage.getNumber());
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
    response.setCount(postsPage.getNumber());
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
    Date endDate = new java.sql.Date(date.getTime() + 86400000);
    Page<Post> postsPage = postRepository.findAllByDate(date, endDate, pageRequest);
    List<Post>  posts = postsPage.getContent();
    for (Post post : posts) {
      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
    }
    response.setCount(postsPage.getNumber());
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
    response.setCount(postsPage.getNumber());
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
}
