package main.repository;


import java.util.Date;
import java.util.List;
import main.api.response.projections.IDateCommentCount;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by size(p.comments) desc")
  Page<Post> findAllPostsByCommentCount(Pageable pageable);

  @Query(value = "SELECT p FROM Post p left join p.votes vote WHERE vote.value = 1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() GROUP BY p order by count(vote) desc")
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

  @Query(value = "SELECT DISTINCT YEAR(time) FROM devblog.posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < CURRENT_TIMESTAMP ORDER BY time",
      nativeQuery = true)
  List<Integer> findAllActiveYears();

  @Query(value = "SELECT DATE(time) AS commentDate, COUNT(time) AS commentCount FROM devblog.posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < CURRENT_TIMESTAMP AND YEAR(time) = :qyear GROUP BY DATE(time) ORDER BY time",
      nativeQuery = true)
  List<IDateCommentCount> findAllDatesWithPosts(@Param("qyear") int qyear);
}
