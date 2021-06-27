package main.repository;

import java.util.List;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  @Query("SELECT u.id FROM User u WHERE u.name = :name")
  List<Integer> findAllByName(@Param("name") String name);

  @Query("SELECT u.id FROM User u WHERE u.email = :email")
  List<Integer> findAllByEmail(@Param("email") String email);
}
