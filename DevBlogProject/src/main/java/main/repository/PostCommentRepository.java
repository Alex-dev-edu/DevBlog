package main.repository;

import java.util.List;
import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

  @Query("SELECT pc FROM PostComment pc WHERE pc.id = :id")
  List<PostComment> findById(@Param("id") int id);
}
