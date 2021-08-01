package main.repository;


import java.util.Date;
import java.util.List;
import main.api.response.projections.IDateCommentCount;
import main.api.response.projections.ILikeDislikeStats;
import main.api.response.projections.IPostsViewsTimeStats;
import main.model.Post;
import main.model.PostVote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()")
  List<Post> findAllLivePosts();

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by size(p.comments) desc")
  Page<Post> findAllPostsByCommentCount(Pageable pageable);

  @Query(value = "SELECT p FROM Post p left join p.votes vote WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() GROUP BY p.id order by CASE WHEN (size(p.votes) = 0) THEN 0 ELSE SUM(vote.value) END desc")
  Page<Post> findAllPostsByLikeCount(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by p.time desc")
  Page<Post> findAllRecentPosts(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by p.time")
  Page<Post> findAllOldPosts(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time<current_timestamp() AND (p.title LIKE %:query% OR p.text LIKE %:query% OR p.user.name LIKE %:query%)")
  Page<Post> findAllPostsContaining(@Param("query") String query, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() and p.time BETWEEN :startdate AND :enddate  order by p.time")
  Page<Post> findAllByDate(@Param("startdate") Date startDate, @Param("enddate") Date endDate,  Pageable pageable);

  @Query(value = "SELECT p FROM Post p left join p.tags t WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() and t.name = :tag order by p.time")
  Page<Post> findAllByTagName(@Param("tag") String tag, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.id = :id")
  List<Post> findPostById(@Param("id") int id);

  @Query(value = "SELECT DISTINCT YEAR(time) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < CURRENT_TIMESTAMP ORDER BY YEAR(time)",
      nativeQuery = true)
  List<Integer> findAllActiveYears();

  @Query(value = "SELECT DATE(time) AS commentDate, COUNT(time) AS commentCount FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < CURRENT_TIMESTAMP AND YEAR(time) = :qyear GROUP BY DATE(time) ORDER BY DATE(time)",
      nativeQuery = true)
  List<IDateCommentCount> findAllDatesWithPosts(@Param("qyear") int qyear);

  @Query(value = "SELECT p FROM Post p WHERE p.userId = :id AND p.isActive = 0 order by p.time")
  Page<Post> findAllInactivePostsById(@Param("id") int id, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.userId = :id AND p.isActive = 1 and p.moderationStatus = 'NEW' order by p.time")
  Page<Post> findAllPendingPostsById(@Param("id") int id, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.userId = :id AND p.isActive = 1 and p.moderationStatus = 'DECLINED' order by p.time")
  Page<Post> findAllDeclinedPostsById(@Param("id") int id, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.userId = :id AND p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by p.time")
  Page<Post> findAllPublishedPostsById(@Param("id") int id, Pageable pageable);

  @Query(value = "SELECT SUM(CASE WHEN (v.value = 1) THEN 1 ELSE 0 END) AS likeCount, SUM(CASE WHEN (v.value = -1) THEN 1 ELSE 0 END) AS dislikeCount FROM Post p left join p.votes v WHERE p.userId = :id and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() GROUP BY p.userId")
  List<ILikeDislikeStats> findLikeDislikeStatsOfUserId(@Param("id") int id);

  @Query(value = "SELECT COUNT(p) AS postCount, SUM(p.viewCount) AS viewCount, MIN(p.time) as oldestPostDate FROM Post p WHERE p.userId = :id GROUP BY p.userId")
  List<IPostsViewsTimeStats>findPostViewsTimeStatsOfUser(@Param("id") int id);

  @Query(value = "SELECT SUM(CASE WHEN (v.value = 1) THEN 1 ELSE 0 END) AS likeCount, SUM(CASE WHEN (v.value = -1) THEN 1 ELSE 0 END) AS dislikeCount FROM PostVote v")
  List<ILikeDislikeStats> findLikeDislikeStatsGlobal();

  @Query(value = "SELECT COUNT(p) AS postCount, SUM(p.viewCount) AS viewCount, MIN(p.time) as oldestPostDate FROM Post p")
  List<IPostsViewsTimeStats>findPostViewsTimeStatsGlobal();

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'NEW' order by p.time")
  Page<Post> findAllNewPosts(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.moderatorId = :modId AND p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by p.time")
  Page<Post> findAllAcceptedPostsByModId(@Param("modId") int modId, Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.moderatorId = :modId AND p.isActive = 1 and p.moderationStatus = 'DECLINED' order by p.time")
  Page<Post> findAllDeclinedPostsByModId(@Param("modId") int modId, Pageable pageable);
}
