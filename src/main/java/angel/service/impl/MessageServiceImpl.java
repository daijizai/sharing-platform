package angel.service.impl;

import angel.dao.MessageDao;
import angel.domain.Message;
import angel.service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, Message> implements MessageService {

    @Autowired
    MessageDao messageDao;


    public List<Message> getMessageByToId(int id){
        return messageDao.listMessagesByToId(id);
    }


}
