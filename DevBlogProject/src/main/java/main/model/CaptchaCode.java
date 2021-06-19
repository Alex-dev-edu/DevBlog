package main.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  @Column(columnDefinition = "TINYTEXT")
  private String code;

  @Column(name = "secret_code", columnDefinition = "TINYTEXT")
  private String secretCode;
}
