package main.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import main.api.response.CalendarResponse;
import main.api.response.PostResponse;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.service.PostService;
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

  public ApiPostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/post")
  private PostResponse post(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String mode) {

    return postService.getPosts(offset, limit, mode);
  }

//  @PreAuthorize("hasAuthority('user:write')")
  @GetMapping("/tag")
  private TagResponse tags(@RequestParam(required = false) String query){
    if (query==null){
      return postService.getTags("");
    }
    return postService.getTags(query);
  }

  @GetMapping("/post/search")
  private PostResponse postSearch(@RequestParam int offset, @RequestParam int limit,
      @RequestParam(required = false) String query) {
    if ((query==null)||(query.replaceAll("\\s+", "").length()==0)){
      return postService.getPosts(offset, limit, "recent");
    }
    return postService.getPostsByQuery(offset, limit, query);
  }

  @GetMapping("/calendar")
  private CalendarResponse calendar(@RequestParam(required = false) Integer year){
    if (year==null){
      return postService.getCalendar(Calendar.getInstance().get(Calendar.YEAR));
    }
    return postService.getCalendar(year);
  }

  @GetMapping("/post/byDate")
  private PostResponse postByDate(@RequestParam int offset, @RequestParam int limit,
      @RequestParam Date date){
    return postService.getPostsByDate(offset, limit, date);
  }

  @GetMapping("/post/byTag")
  private PostResponse postByTag(@RequestParam int offset, @RequestParam int limit,
      @RequestParam String tag){
    return postService.getPostsByTag(offset, limit, tag);
  }

  @GetMapping("/post/{id}")
  private ResponseEntity<SinglePostResponse> postById(@PathVariable int id){
    List<SinglePostResponse> response = postService.getPostById(id);
    if (response.size()==0){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    return new ResponseEntity<>(response.get(0), HttpStatus.OK);
  }
}
