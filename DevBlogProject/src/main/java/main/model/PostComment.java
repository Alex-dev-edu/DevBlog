package main.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "post_comments")
@Data
public class PostComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Nullable
  private int parentId;

  @OneToOne
  private PostComment parent;

  private int postId;

  @ManyToOne
  private Post post;

  private int userId;

  @ManyToOne
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  private String text;
}
