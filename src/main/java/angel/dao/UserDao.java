package angel.dao;

import angel.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    //根据名字获取用户
    @Select("select * from user " +
            "where name=#{name}")
    public User getUserByName(String name);

    //根据【名字片段】获取用户
    @Select("select * from user " +
            "where name like concat('%',#{name},'%')")
    public List<User> listUsersByNameFragment(String name);
}
