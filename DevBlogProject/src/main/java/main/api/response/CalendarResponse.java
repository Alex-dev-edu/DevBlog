package main.api.response;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;
import main.api.response.projections.IDateCommentCount;

@Data
public class CalendarResponse {

  List<Integer> years;

  TreeMap<String, Integer> posts = new TreeMap<>();
}
