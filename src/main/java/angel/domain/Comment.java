package angel.domain;

import lombok.Data;

@Data
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer entityId;
    private Integer entityType;
    private String content;
    private String createdDate;
    private Integer status;

}
