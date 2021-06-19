package main.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
  @Column(name = "parent_id", insertable = false, updatable = false)
  private int parentId;

  @OneToOne
  @JoinColumn(name = "parent_id")
  private PostComment parent;

  @Column(name = "post_id", insertable = false, updatable = false)
  private int postId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  @Column(name = "user_id", insertable = false, updatable = false)
  private int userId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  private String text;
}
