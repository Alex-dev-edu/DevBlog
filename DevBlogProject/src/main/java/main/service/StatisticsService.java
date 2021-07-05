package main.service;

import java.security.Principal;
import java.util.List;
import javassist.NotFoundException;
import main.api.response.StatisticsResponse;
import main.api.response.projections.ILikeDislikeStats;
import main.api.response.projections.IPostsViewsTimeStats;
import main.model.GlobalSetting;
import main.model.User;
import main.repository.GlobalSettingRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private final PostRepository postRepository;
  private final GlobalSettingRepository settingRepository;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public StatisticsService(UserRepository userRepository,
      PostRepository postRepository, GlobalSettingRepository settingRepository,
      AuthenticationManager authenticationManager) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.settingRepository = settingRepository;
    this.authenticationManager = authenticationManager;
  }

  public StatisticsResponse statisticsMy(Principal principal){
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    StatisticsResponse response = new StatisticsResponse();
    List<ILikeDislikeStats> likesDislikes = postRepository.findLikeDislikeStatsOfUserId(user.getId());
    List<IPostsViewsTimeStats> postsViewsTime = postRepository.findPostViewsTimeStatsOfUser(user.getId());
    response.setPostsCount(postsViewsTime.get(0).getPostCount());
    response.setLikesCount(likesDislikes.get(0).getLikeCount());
    response.setDislikesCount(likesDislikes.get(0).getDislikeCount());
    response.setViewsCount(postsViewsTime.get(0).getViewCount());
    response.setFirstPublication(postsViewsTime.get(0).getOldestPostDate().getTime() / 1000);
    return  response;
  }

  public ResponseEntity<StatisticsResponse> statisticsGlobal(Principal principal){
    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
    try {
    GlobalSetting statIsPublic = settingRepository.findByCode("STATISTICS_IS_PUBLIC")
        .orElseThrow(() -> new NotFoundException("SETTING NOT FOUND"));

    if ((user.getIsModerator()!=1) && (!statIsPublic.getValue().equals("YES"))){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    StatisticsResponse response = new StatisticsResponse();
    List<ILikeDislikeStats> likesDislikes = postRepository.findLikeDislikeStatsGlobal();
    List<IPostsViewsTimeStats> postsViewsTime = postRepository.findPostViewsTimeStatsGlobal();
    response.setPostsCount(postsViewsTime.get(0).getPostCount());
    response.setLikesCount(likesDislikes.get(0).getLikeCount());
    response.setDislikesCount(likesDislikes.get(0).getDislikeCount());
    response.setViewsCount(postsViewsTime.get(0).getViewCount());
    response.setFirstPublication(postsViewsTime.get(0).getOldestPostDate().getTime() / 1000);

    return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (NotFoundException ex){
      ex.printStackTrace();
    }
    return ResponseEntity.status(HttpStatus.OK).body(new StatisticsResponse());
  }
}
