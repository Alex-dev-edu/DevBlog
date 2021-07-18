package main.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import main.api.response.CommentErrorResponse;
import main.api.response.CommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PostCommentService {

  private final PostCommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  @Autowired
  public PostCommentService(PostCommentRepository commentRepository,
      PostRepository postRepository, UserRepository userRepository) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  public ResponseEntity<CommentResponse> postComment(Principal principal,
      Integer parentId, Integer postId, String text)
  {
    System.out.println(parentId);
    System.out.println(postId);
    int parent = -1;
    if (parentId!=null){
      parent = parentId;
    }
    List<Post> postList = postRepository.findPostById(postId);
    List<PostComment> postCommentList = commentRepository.findById(parent);

    HashMap<String, String> errors = new HashMap<>();
    if ((postCommentList.size()<1) && (parent!=-1)){
      errors.put("parent", "Указанного комментария-родителя не существует");
    }
    if (postList.size()<1){
      errors.put("post", "Указанного поста не существует");
    }
    if (text.replaceAll("<.*?>", "").length()<21){
      errors.put("text", "Текст публикации слишком короткий");
    }
    if (errors.size()>0){
      CommentErrorResponse errorResponse = new CommentErrorResponse();
      errorResponse.setResult(false);
      errorResponse.setErrors(errors);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));


    PostComment comment = new PostComment();
    comment.setPost(postList.get(0));
    comment.setTime(new java.util.Date(System.currentTimeMillis()));
    comment.setText(text);
    if (parent!=-1){
      comment.setParent(postCommentList.get(0));
    }
    comment.setUser(user);
    commentRepository.save(comment);

    CommentResponse response = new CommentResponse();
    response.setId(comment.getId());
    return ResponseEntity.ok(response);
  }
}
