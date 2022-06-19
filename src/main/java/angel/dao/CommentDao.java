package angel.dao;

import angel.domain.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDao extends BaseMapper<Comment> {

    //根据id和类型查找评论
    @Select("select * from comment " +
            "where entity_id=#{entityId} and entity_type=#{entityType} " +
            "order by id desc")
    List<Comment> listComments(@Param("entityId") int entityId, @Param("entityType") int entityType);

    //获取评论总数
    @Select("select count(id) from comment " +
            "where entity_id=#{entityId} and entity_type=#{entityType}")
    int countComment(@Param("entityId") int entityId, @Param("entityType") int entityType);


}
