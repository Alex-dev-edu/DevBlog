package main.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tag2post")
@Data
public class Tag2Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int postId;

  @ManyToOne
  private Post post;

  private int tagId;

  @ManyToOne
  private Tag tag;
}
