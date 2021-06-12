package main.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String isModerator;

  @Temporal(TemporalType.TIMESTAMP)
  private Date regTime;

  private String name;

  private String email;

  private String password;

  @Nullable
  private String code;

  @Nullable
  private String photo;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<Post> posts;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "voters")
  private List<Post> votedPosts;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<PostVote> votes;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<PostComment> comments;
}
