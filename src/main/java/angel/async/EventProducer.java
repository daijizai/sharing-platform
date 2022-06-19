package angel.async;

import angel.util.JedisAdapter;
import angel.util.RedisKeyUtil;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author daijizai
 * @version 1.0
 * @date 2022/5/20 20:19
 * @description 收到一个event，序列化event，把这个序列化后的string放到队列里面去
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String message = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
