package main.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import main.api.response.CalendarResponse;
import main.api.response.PostResponse;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.service.PostService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {

  private final PostService postService;
  private final UserService userService;
  @Autowired
  public ApiPostController(PostService postService, UserService userService) {
    this.postService = postService;
    this.userService = userService;
  }

  @GetMapping("/post")
  @PreAuthorize("hasAuthority('user:write')")
  public PostResponse post(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String mode) {

    return postService.getPosts(offset, limit, mode);
  }


  @GetMapping("/tag")
  public TagResponse tags(@RequestParam(required = false) String query){
    if (query==null){
      return postService.getTags("");
    }
    return postService.getTags(query);
  }

  @GetMapping("/post/search")
  public PostResponse postSearch(@RequestParam int offset, @RequestParam int limit,
      @RequestParam(required = false) String query) {
    if ((query==null)||(query.replaceAll("\\s+", "").length()==0)){
      return postService.getPosts(offset, limit, "recent");
    }
    return postService.getPostsByQuery(offset, limit, query);
  }

  @GetMapping("/calendar")
  public CalendarResponse calendar(@RequestParam(required = false) Integer year){
    if (year==null){
      return postService.getCalendar(Calendar.getInstance().get(Calendar.YEAR));
    }
    return postService.getCalendar(year);
  }

  @GetMapping("/post/byDate")
  public PostResponse postByDate(@RequestParam int offset, @RequestParam int limit,
      @RequestParam Date date){
    return postService.getPostsByDate(offset, limit, date);
  }

  @GetMapping("/post/byTag")
  public PostResponse postByTag(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String tag){
    return postService.getPostsByTag(offset, limit, tag);
  }

  @GetMapping("/post/{id}")
  public ResponseEntity<SinglePostResponse> postById(@PathVariable int id){
    List<SinglePostResponse> response = postService.getPostById(id);
    if (response.size()==0){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return new ResponseEntity<>(response.get(0), HttpStatus.OK);
  }


  @GetMapping("/post/my")
  @PreAuthorize("hasAuthority('user:write')")
  public PostResponse postsMy(Principal principal, @RequestParam int offset, @RequestParam int limit,
      @RequestParam String status){
    return userService.getMyPosts(principal, limit, offset, status);
  }
}
