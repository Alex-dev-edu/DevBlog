package main.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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

  @Column(name = "is_moderator", columnDefinition = "TINYINT")
  private int isModerator;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "reg_time")
  private Date regTime;

  private String name;

  private String email;

  private String password;

  @Nullable
  private String code;

  @Nullable
  @Column(columnDefinition = "TEXT")
  private String photo;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<Post> posts;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "voters")
  private List<Post> votedPosts;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  private List<PostVote> votes;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  private List<PostComment> comments;

  public Role getRole(){
    return  isModerator == 1 ? Role.MODERATOR : Role.USER;
  }
}
