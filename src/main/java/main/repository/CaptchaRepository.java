package main.repository;

import java.util.List;
import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

  @Query("SELECT c.id FROM CaptchaCode c WHERE c.code = :code and c.secretCode = :secret")
  List<Integer> findCaptcha(@Param("code") String code, @Param("secret") String secret);

  @Transactional
  @Modifying
  @Query("DELETE FROM CaptchaCode c WHERE c.time < (current_timestamp() + 3600000)")
  void deleteOldCaptcha();
}
