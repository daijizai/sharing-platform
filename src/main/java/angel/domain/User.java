package angel.domain;

import lombok.Data;

@Data
public class User {
    public Integer id;
    public String name;
    public String password;
    public String salt;
    public String headUrl;
}
