package main.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import main.api.request.PostRequest;
import main.api.response.CalendarResponse;
import main.api.response.PostImageResponse;
import main.api.response.PostResponse;
import main.api.response.RegisterResponse;
import main.api.response.SinglePostResponse;
import main.api.response.TagResponse;
import main.service.PostService;
import main.service.UserService;
import main.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiPostController {

  private final PostService postService;
  private final UserService userService;
  private final VoteService voteService;
  @Autowired
  public ApiPostController(PostService postService, UserService userService,
      VoteService voteService) {
    this.postService = postService;
    this.userService = userService;
    this.voteService = voteService;
  }

  @GetMapping("/post")
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

  @PostMapping("/post/like")
  @PreAuthorize("hasAuthority('user:write')")
  public RegisterResponse postLike(Principal principal, @RequestParam int post_id){
    return voteService.like(principal, post_id);
  }

  @PostMapping("/post/dislike")
  @PreAuthorize("hasAuthority('user:write')")
  public RegisterResponse postDislike(Principal principal, @RequestParam int post_id){
    return voteService.dislike(principal, post_id);
  }

  @PostMapping("/post")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public RegisterResponse postNew(Principal principal, @RequestBody PostRequest request){
    return postService.post(principal, request);
  }

  @RequestMapping(value = "/image", method = RequestMethod.POST, consumes = {"multipart/form-data"})
  @PreAuthorize("hasAuthority('user:write')")
  public PostImageResponse postImage(@RequestParam MultipartFile file){
    return postService.postImage(file);
  }

  @RequestMapping(value = "/post/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public RegisterResponse postUpdate(Principal principal, @RequestBody PostRequest request, @PathVariable int id){
    return postService.changePost(principal, request, id);
  }

  @GetMapping("/post/moderation")
  public PostResponse getModeration(Principal principal,
      @RequestParam int offset, @RequestParam int limit, @RequestParam String status)
  {
    return postService.getModerationPosts(principal, offset, limit, status);
  }

  @PostMapping("/post/moderation")
  public RegisterResponse postModeration(Principal principal,
      @RequestParam int post_id, @RequestParam String decision)
  {
    return postService.postModeration(principal, post_id, decision);
  }
}
