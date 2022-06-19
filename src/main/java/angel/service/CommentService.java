package angel.service;

import angel.domain.Comment;
import angel.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {

    List<Comment> listComments(int entityId, int entityType);

    int countComment(int entityId, int entityType);

}
