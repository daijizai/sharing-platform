package angel.controller;

import angel.async.EventModel;
import angel.async.EventProducer;
import angel.async.EventType;
import angel.domain.EntityType;
import angel.domain.HostHolder;
import angel.domain.News;
import angel.service.LikeService;
import angel.service.NewsService;
import angel.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    //根据newsid点赞
    @GetMapping("/like")
    @ResponseBody
    public String like(@RequestParam("id") int newsId) {

        try {

            //获取当前登录用户的id 用户未登录时报错
            int userId = hostHolder.getUser().getId();

            //获得该名登录用户对本条news的喜欢状态 【喜欢返回1 不喜欢返回-1 否则返回0】
            int likeStatus = likeService.getLikeStatus(userId, EntityType.ENTITY_NEWS, newsId);

            if (likeStatus == 1) {
                return ToutiaoUtil.getJSONString(1, "请勿重复操作");
            }

            long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);

            //更新mysql中的likeCount
            newsService.updateLikeCount(newsId, (int) likeCount);

            News news = newsService.getById(newsId);
            System.out.println("newsId:" + newsId);

            eventProducer.fireEvent(
                    new EventModel(EventType.LIKE)
                            .setActorId(hostHolder.getUser().getId())
                            .setEntityId(newsId)
                            .setEntityType(EntityType.ENTITY_NEWS)
                            .setEntityOwnerId(news.userId)
            );

            return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LikeController-like");
            return ToutiaoUtil.getJSONString(1, "赞同异常");
        }
    }

    //根据newsid取消点赞
    @GetMapping("/dislike")
    @ResponseBody
    public String dislike(@RequestParam("id") int newsId) {

        try {

            //获取当前登录用户的id 用户未登录时报错
            int userId = hostHolder.getUser().getId();

            //获得该名登录用户对本条news的喜欢状态 【喜欢返回1 不喜欢返回-1 否则返回0】
            int likeStatus = likeService.getLikeStatus(userId, EntityType.ENTITY_NEWS, newsId);

            if (likeStatus == -1) {
                return ToutiaoUtil.getJSONString(1, "请勿重复操作");
            }

            long likeCount = likeService.dislike(userId, EntityType.ENTITY_NEWS, newsId);

            //更新mysql中的likecount
            newsService.updateLikeCount(newsId, (int) likeCount);

            return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LikeController-dislike");
            return ToutiaoUtil.getJSONString(1, "反对异常");
        }
    }
}


