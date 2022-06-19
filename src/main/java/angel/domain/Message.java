package angel.domain;

import lombok.Data;

@Data
public class Message {
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String content;
    private String createdDate;
    private Integer hasRead;
    private String conversationId;

}
