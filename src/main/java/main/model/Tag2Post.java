package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

  @Column(name = "post_id", insertable = false, updatable = false)
  private int postId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  @Column(name = "tag_id", insertable = false, updatable = false)
  private int tagId;

  @ManyToOne
  @JoinColumn(name = "tag_id")
  private Tag tag;
}
