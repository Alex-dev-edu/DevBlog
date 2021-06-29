package main.repository;

import java.util.List;
import java.util.Optional;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u.id FROM User u WHERE u.name = :name")
  List<Integer> findAllByName(@Param("name") String name);

  @Query("SELECT u.id FROM User u WHERE u.email = :email")
  List<Integer> findAllByEmail(@Param("email") String email);
}
