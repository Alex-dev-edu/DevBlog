package main.utils;

import main.api.response.dto.PostDTO;
import main.model.Post;
import main.model.PostComment;
import main.model.PostVote;

public class MappingUtils {

  public static PostDTO mapPostToPostDTO(Post post) {
    PostDTO postDTO = new PostDTO();
    postDTO.setId(post.getId());
    postDTO.setTimestamp(post.getTime().getTime() / 1000);
    postDTO.getUser().setId(post.getUser().getId());
    postDTO.getUser().setName(post.getUser().getName());
    postDTO.setTitle(post.getTitle());
    postDTO.setAnnounce(post.getText().substring(Math.min(post.getText().length(), 150)));
    int likeCount = 0;
    int dislikeCount = 0;
    for (PostVote vote : post.getVotes()) {
      if (vote.getValue() == 1) {
        likeCount++;
      }
      if (vote.getValue() == -1) {
        likeCount++;
      }
    }
    postDTO.setLikeCount(likeCount);
    postDTO.setDislikeCount(dislikeCount);
    postDTO.setCommentCount(post.getComments().size());
    postDTO.setViewCount(post.getViewCount());
    return postDTO;
  }
}
