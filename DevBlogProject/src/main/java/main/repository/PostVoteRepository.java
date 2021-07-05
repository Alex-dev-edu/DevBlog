package main.repository;

import java.util.List;
import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

  @Query(value = "SELECT v FROM PostVote v WHERE v.userId = :userId and v.postId = :postId")
  List<PostVote> findVoteByUserAndPostId(@Param("userId") int userId, @Param("postId") int postId);
}
