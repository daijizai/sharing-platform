package angel.async.handler;

import angel.async.EventHandler;
import angel.async.EventModel;
import angel.async.EventType;
import angel.domain.Message;
import angel.domain.User;
import angel.service.MessageService;
import angel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * @author daijizai
 * @version 1.0
 * @date 2022/5/23 15:42
 * @description
 */
@Component
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();

        message.setToId(model.getActorId());
        message.setContent("登录成功");
        // SYSTEM ACCOUNT
        message.setFromId(3);
        String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        message.setCreatedDate(timeStr1);
        message.setConversationId(message.getToId()+"_"+message.getFromId());
        messageService.save(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);//关注登录操作
    }
}
