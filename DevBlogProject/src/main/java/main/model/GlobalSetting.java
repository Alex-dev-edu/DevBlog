package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "global_settings")
@Data
public class GlobalSetting {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String code;

  private String name;

  private String value;
}
