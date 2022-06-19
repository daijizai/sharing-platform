package angel.dao;

import angel.domain.Image;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImageDao extends BaseMapper<Image> {

    @Select("select * from image where id=(select MAX(id) from image )")
    Image getLastImage();

}
