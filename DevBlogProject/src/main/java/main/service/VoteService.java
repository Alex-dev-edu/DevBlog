package main.service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import main.api.response.RegisterResponse;
import main.model.PostVote;
import main.model.User;
import main.repository.PostVoteRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

  private final PostVoteRepository voteRepository;
  private final UserRepository userRepository;

  @Autowired
  public VoteService(PostVoteRepository voteRepository,
      UserRepository userRepository) {
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public RegisterResponse like(Principal principal, int postId){
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    List<PostVote> vote = voteRepository.findVoteByUserAndPostId(user.getId(), postId);
    RegisterResponse response = new RegisterResponse();
    if (vote.size()>=1){
      if (vote.get(0).getValue()==1) {
        response.setResult(false);
        return response;
      } else {
        voteRepository.deleteById(vote.get(0).getId());
      }
    }

    PostVote newVote = new PostVote();
    newVote.setUserId(user.getId());
    newVote.setPostId(postId);
    newVote.setValue(1);
    newVote.setTime(new Date(System.currentTimeMillis()));
    voteRepository.save(newVote);
    response.setResult(true);
    return response;
  }

  @Transactional
  public RegisterResponse dislike(Principal principal, int postId){
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    List<PostVote> vote = voteRepository.findVoteByUserAndPostId(user.getId(), postId);
    RegisterResponse response = new RegisterResponse();
    if (vote.size()>=1){
      if (vote.get(0).getValue()==-1) {
        response.setResult(false);
        return response;
      } else {
        voteRepository.deleteById(vote.get(0).getId());
      }
    }

    PostVote newVote = new PostVote();
    newVote.setUserId(user.getId());
    newVote.setPostId(postId);
    newVote.setValue(-1);
    newVote.setTime(new Date(System.currentTimeMillis()));
    voteRepository.save(newVote);
    response.setResult(true);
    return response;
  }
}
