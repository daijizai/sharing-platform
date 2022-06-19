package angel.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

//在spring初始化bean的时候，如果bean实现了InitializingBean接口，会自动调用afterPropertiesSet方法
@Service
public class JedisAdapter implements InitializingBean {
    private JedisPool pool=null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool=new JedisPool(new GenericObjectPoolConfig(),"localhost",6379,10000, null,0);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    //添加 【点赞】
    public long sadd(String key,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.sadd(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //删除 【取消点赞】
    public long srem(String key,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.srem(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //是否存在
    public boolean sismember(String key,String value){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.sismember(key,value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //赞的数量
    public long scard(String key){
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key,Object obj){
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
