package angel.service.impl;

import angel.service.LikeService;
import angel.util.JedisAdapter;
import angel.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 获得用户的喜欢状态
     * 如果喜欢返回1 不喜欢返回-1 否则返回0
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;//如果用户的id出现在喜欢列表里 代表这个用户喜欢
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))?-1:0;
    }

    //喜欢
    public long like(int userId,int entityType,int entityId){
        //把用户id加入喜欢列表
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));

        //把用户id从不喜欢列表删除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);//返回有多少人喜欢
    }

    //不喜欢
    public long dislike(int userId,int entityType,int entityId){
        //把用户id从喜欢列表删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));

        //把用户id加入不喜欢列表
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

}
