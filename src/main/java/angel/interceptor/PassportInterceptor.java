package angel.interceptor;

import angel.dao.TicketDao;
import angel.dao.UserDao;
import angel.domain.HostHolder;
import angel.domain.Ticket;
import angel.domain.User;
import angel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 用于验证用户是否登录
 * Interceptor:拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String ticket = null;//ticket default is null
        if (request.getCookies() == null) {
            return true;
        }
        for (Cookie cookie : request.getCookies()) {//遍历req中的cookies
            if ("ticket".equals(cookie.getName())) {//如果cookie名称为【ticket】
                ticket = cookie.getValue();//获取【ticket】对应的值
                break;
            }
        }

        if (ticket != null) {//如果ticket不为空 去数据库中检索 防止伪造
            Ticket realLoginTicket = ticketDao.getByTicket(ticket);

            boolean illegal = realLoginTicket == null //数据库查不到
                    ||realLoginTicket.getExpired().before(new Date())
                    || realLoginTicket.getStatus() != 0;//ticket失效
            if (illegal) {
                return true;
            }

            //根据 从ticket中获取的userid 获取user对象
            User user = userDao.selectById(realLoginTicket.getUserId());


            hostHolder.setUser(user);//设置当前登录用户
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
//        if(modelAndView!=null&& hostHolder.getUser()!=null){
//            System.out.println("postHandle:"+hostHolder.getUser());
//            modelAndView.addObject("user",hostHolder.getUser());
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        hostHolder.clear();
    }
}
