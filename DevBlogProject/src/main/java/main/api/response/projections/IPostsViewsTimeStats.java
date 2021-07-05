package main.api.response.projections;

import java.util.Date;

public interface IPostsViewsTimeStats {
  Integer getPostCount();
  Integer getViewCount();
  Date getOldestPostDate();
}
