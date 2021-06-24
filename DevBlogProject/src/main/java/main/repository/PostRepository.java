package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by size(p.comments) desc", nativeQuery = true)
  Page<Post> findAllPostsByCommentCount(Pageable pageable);

  @Query(value = "SELECT p FROM Post p left join p.votes vote WHERE vote.value = 1 and p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp() order by count(vote) desc", nativeQuery = true)
  Page<Post> findAllPostsByLikeCount(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by p.time desc")
  Page<Post> findAllRecentPosts(Pageable pageable);

  @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time<current_timestamp()  order by p.time", nativeQuery = true)
  Page<Post> findAllOldPosts(Pageable pageable);
}
