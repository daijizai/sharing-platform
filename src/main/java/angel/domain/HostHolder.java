package angel.domain;

import org.springframework.stereotype.Component;

//表示当前的用户是谁
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();
    public User getUser() {
        return users.get();
    }
    public void setUser(User user) {
        users.set(user);
    }
    public void clear() {
        users.remove();
    }
}
