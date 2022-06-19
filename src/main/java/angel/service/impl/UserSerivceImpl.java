package angel.service.impl;

import angel.dao.TicketDao;
import angel.dao.UserDao;
import angel.domain.Ticket;
import angel.domain.User;
import angel.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
public class UserSerivceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TicketDao TicketDao;

    /**
     * 注册
     *
     * @param user
     * @return
     */
    public Map<String, Object> register(User user, int expiredSeconds) {

        //用于返回问题信息
        HashMap<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(user.getName())) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("msg", "密码不能为空");
            return map;
        }

        //数据库中是否能查到相同用户名的数据
        User realUser = userDao.getUserByName(user.getName());
        if (realUser != null) {
            map.put("msg", "用户名已经被注册了");
            return map;
        }

        //此处用于数据添加
        String salt = UUID.randomUUID().toString().substring(0, 5);
        user.setSalt(salt);
        String EncryptedPassword = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
        user.setPassword(EncryptedPassword);

        Random r = new Random();
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", r.nextInt(1000)));

        userDao.insert(user);

        //登录
        String ticket = addLoginTicket(user.getId(), expiredSeconds);
        map.put("ticket", ticket);


        return map;

    }


    /**
     * 登录
     *
     * @param user
     * @return
     */
    public Map<String, Object> login(User user, int expiredSeconds) {

        //用于返回问题信息
        HashMap<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(user.getName())) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("msg", "密码不能为空");
            return map;
        }

        //数据库中是否能查到相同用户名的数据
        //realUser是从数据库中查到的 所以一定是real的
        User realUser = userDao.getUserByName(user.getName());
        if (realUser == null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        //判断密码
        String realPassword = realUser.getPassword();
        String uncertainPassword = DigestUtils.md5DigestAsHex((user.getPassword() + realUser.getSalt()).getBytes());
        if (!realPassword.equals(uncertainPassword)) {
            map.put("msg", "密码不正确");
            return map;
        }

        //全部验证之后 剩下的操作使用realUser


        //ticket
//        System.out.println(realUser);
        String ticket = addLoginTicket(realUser.getId(), expiredSeconds);
        map.put("ticket", ticket);

        map.put("userId", realUser.getId());
        return map;

    }

    /**
     * 获取ticket用于加入cookie供登录和注册时使用
     *
     * @param userId
     * @return
     */
    public String addLoginTicket(int userId, int expiredSeconds) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * expiredSeconds);
        ticket.setExpired(date);//设置过期时间
        ticket.setStatus(0);//默认为0
        ticket.setTicket(UUID.randomUUID().toString().replace("-", ""));

        TicketDao.insert(ticket);
        return ticket.getTicket();
    }


    /**
     * 登出
     *
     * @param ticket
     */
    public void logout(String ticket) {
        TicketDao.updateStatus(ticket, 1);
    }

    //根据name获取user
    public User getUserByName(String name) {
        return userDao.getUserByName(name);
    }


}
