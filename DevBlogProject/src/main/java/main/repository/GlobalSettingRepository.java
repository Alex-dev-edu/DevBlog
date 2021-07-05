package main.repository;

import java.util.Optional;
import main.model.GlobalSetting;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingRepository extends JpaRepository<GlobalSetting, Integer> {

  Optional<GlobalSetting> findByCode(String code);

  Optional<GlobalSetting> findById(int id);
}
