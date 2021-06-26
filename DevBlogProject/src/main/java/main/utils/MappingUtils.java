package main.utils;

import java.util.ArrayList;
import java.util.List;
import main.api.response.dto.CommentDTO;
import main.api.response.dto.PostDTO;
import main.api.response.SinglePostResponse;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.PostVote;
import main.model.Tag;

public class MappingUtils {

  public static PostDTO mapPostToPostDTO(Post post) {
    PostDTO postDTO = new PostDTO();
    postDTO.setId(post.getId());
    postDTO.setTimestamp(post.getTime().getTime() / 1000);
    postDTO.getUser().setId(post.getUser().getId());
    postDTO.getUser().setName(post.getUser().getName());
    postDTO.setTitle(post.getTitle());
    String announce = ((post.getText()).substring(0, Math.min(post.getText().length(), 150))).replaceAll("<.*?>", "")  + "...";
    postDTO.setAnnounce(announce);
    int likeCount = 0;
    int dislikeCount = 0;
    for (PostVote vote : post.getVotes()) {
      if (vote.getValue() == 1) {
        likeCount++;
      }
      if (vote.getValue() == -1) {
        dislikeCount++;
      }
    }
    postDTO.setLikeCount(likeCount);
    postDTO.setDislikeCount(dislikeCount);
    postDTO.setCommentCount(post.getComments().size());
    postDTO.setViewCount(post.getViewCount());
    return postDTO;
  }

  public static SinglePostResponse mapPostToSinglePostDTO(Post post){
    SinglePostResponse postDTO = new SinglePostResponse();
    postDTO.setId(post.getId());
    postDTO.setActive(
        (post.getIsActive() == 1) && (post.getModerationStatus() == ModerationStatus.ACCEPTED) && (
            post.getTime().getTime() <= System.currentTimeMillis()));
    postDTO.setTimestamp(post.getTime().getTime() / 1000);
    postDTO.getUser().setId(post.getUser().getId());
    postDTO.getUser().setName(post.getUser().getName());
    postDTO.setTitle(post.getTitle());
    postDTO.setText(post.getText());
    int likeCount = 0;
    int dislikeCount = 0;
    for (PostVote vote : post.getVotes()) {
      if (vote.getValue() == 1) {
        likeCount++;
      }
      if (vote.getValue() == -1) {
        dislikeCount++;
      }
    }
    postDTO.setLikeCount(likeCount);
    postDTO.setDislikeCount(dislikeCount);
    postDTO.setViewCount(post.getViewCount());
    List<CommentDTO> commentsDTO= new ArrayList<>();
    for (PostComment comment : post.getComments()){
      CommentDTO commentDTO = new CommentDTO();
      commentDTO.setId(comment.getId());
      commentDTO.setText(comment.getText());
      commentDTO.setTimestamp(comment.getTime().getTime() / 1000);
      commentDTO.getUser().setId(comment.getUserId());
      commentDTO.getUser().setName(comment.getUser().getName());
      commentDTO.getUser().setPhoto(comment.getUser().getPhoto());
      commentsDTO.add(commentDTO);
    }
    postDTO.setComments(commentsDTO);
    List<String> tags = new ArrayList<>();
    for (Tag tag : post.getTags()){
      tags.add(tag.getName());
    }
    postDTO.setTags(tags);
    return postDTO;
  }
}
