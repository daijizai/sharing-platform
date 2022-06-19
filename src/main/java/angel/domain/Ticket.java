package angel.domain;


import lombok.Data;

import java.util.Date;

@Data
public class Ticket {
    private Integer id;
    private Integer userId;
    private Date expired;
    private Integer status;//0 有效；1 无效
    private String ticket;
}
