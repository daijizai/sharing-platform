package angel.controller;

import angel.async.EventModel;
import angel.async.EventProducer;
import angel.async.EventType;
import angel.domain.HostHolder;
import angel.domain.User;
import angel.service.UserService;
import angel.util.ToutiaoConstant;
import angel.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController implements ToutiaoConstant {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    //注册
    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody User user,
                           @RequestParam("rememberme") String rememberme,
                           HttpServletResponse response) {

        try {
            int expiredSeconds = "true".equals(rememberme) ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

            //在userService中执行注册的操作 返回结果
            Map<String, Object> map = userService.register(user,expiredSeconds);

            //正常情况下应该有ticket
            if (map.containsKey("ticket")) {
//                System.out.println("register-ticket:"+map.get("ticket"));
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//设置为全站有效
                if ("true".equals(rememberme)) {
                    cookie.setMaxAge(expiredSeconds);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                //没有ticket原因【用户名或密码为空 用户名重复】
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LoginController-register");
            return ToutiaoUtil.getJSONString(2, "注册异常");
        }

    }

    //登录
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestBody User user,
                        @RequestParam("rememberme") String rememberme,
                        HttpServletResponse response) {

        try {
            int expiredSeconds = "true".equals(rememberme) ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

            Map<String, Object> map = userService.login(user,expiredSeconds);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//设置为全站有效
                if ("true".equals(rememberme)) {
                    cookie.setMaxAge(expiredSeconds);
                }
                response.addCookie(cookie);

                hostHolder.setUser(user);//设置当前登录用户
                System.out.println("login-设置当前登录用户-" + user.getName());

                eventProducer.fireEvent(
                        new EventModel(EventType.LOGIN)
                                .setActorId((int) map.get("userId"))
                );

                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                //没有ticket原因【用户名为空或不存在 密码为空或错误】
                return ToutiaoUtil.getJSONString(1, map);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LoginController-login");
            return ToutiaoUtil.getJSONString(2, "登录异常");
        }

    }


    //退出
    @GetMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request) {

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return ToutiaoUtil.getJSONString(1, "Cookie[] cookies为空");
            }

            for (Cookie cookie : cookies) {
                if ("ticket".equals(cookie.getName())) {
                    String ticket = cookie.getValue();

                    userService.logout(ticket);

                    return ToutiaoUtil.getJSONString(0, "退出成功");
                }
            }

            return ToutiaoUtil.getJSONString(1, "cookies中没有ticket");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LoginController-logout");
            return ToutiaoUtil.getJSONString(1, "退出异常");

        }

    }

    //前端钩子函数调用 获取用户登录状态
    @GetMapping("/loginStatus")
    @ResponseBody
    public String loginStatus() {
        if (hostHolder.getUser() == null) {
            //未登录返回1
            return ToutiaoUtil.getJSONString(1);
        }
        //已登录返回0
        return ToutiaoUtil.getJSONString(0);
    }

}
