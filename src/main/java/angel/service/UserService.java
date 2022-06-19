package angel.service;

import angel.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService extends IService<User> {

    Map<String,Object> register(User user,int expiredSeconds);

    Map<String,Object> login(User user,int expiredSeconds);

    void logout(String ticket);

    User getUserByName(String name);

    String addLoginTicket(int userId, int expiredSeconds);
}
