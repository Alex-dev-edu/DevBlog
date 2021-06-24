package main.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import main.api.response.PostResponse;
import main.api.response.TagResponse;
import main.api.response.dto.PostDTO;
import main.api.response.dto.TagDTO;
import main.model.ModerationStatus;
import main.model.Post;
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
// maybe livePosts should contain copies of the Post objects (V●ᴥ●V)
    for (Post post : postRepository.findAll()) {
      if (post.getModerationStatus().equals(ModerationStatus.ACCEPTED) && (post.getTime().getTime()
          >= System.currentTimeMillis())){
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


    return response;
  }

  public PostResponse getPosts(int offset, int limit, String mode) {
    PostResponse response = new PostResponse();
    List<PostDTO> responsePosts = new ArrayList<>();

//    postRepository.findAll().forEach(posts::add);
//
//    for (Post post : posts) {
//      responsePosts.add(MappingUtils.mapPostToPostDTO(post));
//    }
//    switch (mode) {
//      case "popular":
//        responsePosts.sort(Comparator.comparingInt(PostDTO::getCommentCount));
//        break;
//      case "best":
//        responsePosts.sort(Comparator.comparingInt(PostDTO::getLikeCount));
//        break;
//      case "early":
//        responsePosts.sort(Comparator.comparingLong(PostDTO::getTimestamp).reversed());
//        break;
//      default:
//        responsePosts.sort(Comparator.comparingLong(PostDTO::getTimestamp));
//    }
//
//    response.setCount(responsePosts.size());
//    response.setPosts(responsePosts.subList(Math.min(responsePosts.size(), offset),
//        Math.min(responsePosts.size(), offset + limit)));


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
}
