package main.api.response.projections;

import java.util.Date;

public interface IDateCommentCount {
  Date getCommentDate();
  Integer getCommentCount();
}
