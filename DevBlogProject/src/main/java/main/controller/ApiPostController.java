package main.controller;

import main.api.response.PostResponse;
import main.api.response.TagResponse;
import main.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {
  private final PostService postService;

  public ApiPostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/post")
  private PostResponse post(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String mode) {

    return postService.getPosts(offset, limit, mode);
  }

  @GetMapping("/tag")
  private TagResponse tags(@RequestParam(required = false) String query){
    if (query==null){
      return postService.getTags("");
    }
    return postService.getTags(query);
  }
}
