package angel.domain;

import lombok.Data;

import java.util.Date;

@Data
public class News {
    public Integer id;
    public String title;
    public String link;
    public Integer imageId;
    public Integer likeCount;
    public Integer commentCount;
    public String createdDate;
    public Integer userId;
}
