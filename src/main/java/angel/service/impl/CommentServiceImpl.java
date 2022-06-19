package angel.service.impl;

import angel.dao.CommentDao;
import angel.domain.Comment;
import angel.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao,Comment> implements CommentService {
    @Autowired
    private CommentDao commentDao;

    public List<Comment> listComments(int entityId, int entityType){
        return commentDao.listComments(entityId,entityType);
    }

    public int countComment(int entityId, int entityType){
        return commentDao.countComment(entityId,entityType);
    }


}
