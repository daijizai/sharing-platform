package angel.dao;

import angel.domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDao extends BaseMapper<Message> {

    //根据接收私信的用户的id获取私信
    @Select("select * from message " +
            "where to_id=#{id} " +
            "order by id desc")
    public List<Message> listMessagesByToId(int id);


}
