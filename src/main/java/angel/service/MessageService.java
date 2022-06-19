package angel.service;

import angel.domain.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MessageService extends IService<Message> {

    List<Message> getMessageByToId(int id);
}
