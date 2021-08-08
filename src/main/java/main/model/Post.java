package main.model;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "posts")
@Data
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "is_active", columnDefinition = "TINYINT")
  private int isActive;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "ENUM('NEW', 'ACCEPTED', 'DECLINED')")
  private ModerationStatus moderationStatus;

  @Nullable
  @Column(name = "moderator_id", insertable = false, updatable = false)
  private Integer moderatorId;

  @ManyToOne
  private User moderator;

  @Column(name = "user_id", insertable = false, updatable = false)
  private int userId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  private String title;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String text;

  @Column(name = "view_count")
  private int viewCount;

  @ManyToMany
  @JoinTable(name = "post_votes",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "user_id")}
  )
  private List<User> voters;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
  private List<PostVote> votes;

  @ManyToMany
  @JoinTable(name = "tag2post",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")}
  )
  private List<Tag> tags;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
  private List<Tag2Post> tag2PostList;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
  private List<PostComment> comments;
}
