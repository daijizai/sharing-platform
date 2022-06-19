package angel.dao;

import angel.domain.Image;
import angel.domain.News;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NewsDao extends BaseMapper<News> {

    //根据标题分页查询之查询新闻总数
    @Select({"<script> " +
            "select count(*) from news " +
            "<where>" +
            "<if test='title!=null and title!=\"\" '>" +
            " title like concat('%',#{title},'%')</if>" +
            "</where>" +
            "</script>"})
    int countNewsByTitle(String title);

    //根据标题分页查询之查询news
    @Select({"<script> " +
            "select * from news " +
            "<where>" +
            "<if test='title!=null and title!=\"\" '>" +
            " title like concat('%',#{title},'%')</if>" +
            "</where>" +
            "order by id DESC " +
            "limit #{begin},#{size}" +
            "</script>"})
    List<News> listNewsByPageAndTitle(@Param("title") String title,
                                      @Param("begin") int begin, @Param("size") int size);


    //根据newsid更新comment_count
    @Update("update news set comment_count=#{commentCount} " +
            "where id=#{id}")
    void updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    //更新news的like数
    @Update("update news set like_count=#{likeCount} " +
            "where id=#{newsId}")
    void updateLikeCount(@Param("newsId") int newsId, @Param("likeCount") int likeCount);

    //更新最新news的imageid
    @Update("update news set image_id=#{imageId} " +
            "where id=(select max_id from(select MAX(id) as max_id from news) as a)")
    void updateImageId(int imageId);


}
