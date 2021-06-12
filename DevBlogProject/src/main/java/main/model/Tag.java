package main.model;

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
import lombok.Data;

@Entity
@Table(name = "tags")
@Data
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
  private List<Post> posts;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
  private List<Tag2Post> tag2PostList;
}
