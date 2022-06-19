package angel.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data//必须加上这个？？？
public class ViewObject {
    private Map<String,Object> viewObject=new HashMap<>();

    public void set(String key,Object value){
        viewObject.put(key,value);
    }

    public Object get(String key){
        return viewObject.get(key);
    }

    //以前【用户、私信】、【用户、评论】、【用户、新闻】每组都要封装成一个ViewObject



}
