package main.repository;

import java.util.Optional;
import main.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
  Optional<Tag> findByName(String name);
}
