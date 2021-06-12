package main.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "post_votes")
@Data
public class PostVote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private int userId;

  @ManyToOne
  private User user;

  private int postId;

  @ManyToOne
  private Post post;

  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  private int value;
}
