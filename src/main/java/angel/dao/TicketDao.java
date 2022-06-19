package angel.dao;

import angel.domain.Ticket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TicketDao extends BaseMapper<Ticket> {

    //更新ticket的状态
    @Update("update ticket set status=#{status} " +
            "where ticket=#{ticket}")
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);

    //根据ticket字段查找
    @Select("select * from ticket " +
            "where ticket=#{ticket}")
    Ticket getByTicket(String ticket);
}
