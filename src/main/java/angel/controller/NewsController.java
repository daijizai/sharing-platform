package angel.controller;

import angel.domain.*;
import angel.service.*;
import angel.util.ToutiaoUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@CrossOrigin
public class NewsController {

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;


    //根据newsid获取图片
    @GetMapping ("/getImage")
    @ResponseBody
    public void getImage(@RequestParam("newsId")String newsId,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");

            String filename = newsService.getImageNameByNewsId(newsId);

            //获得照片的url
            String finalUrl  = qiniuService.getImage(filename);

            //根据image的name拿到图片 传到前端
//            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR+filename)),
//                    response.getOutputStream());

            //重定向 进行资源跳转
            response.sendRedirect(finalUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //上传图片
    @PostMapping("/uploadImage")
    @ResponseBody
    public void uploadImage(@RequestParam MultipartFile file){
        try {

            System.out.println("------------------------");
            System.out.println("getBytes："+file.getBytes());
            System.out.println("getName："+file.getName());
            System.out.println("getOriginalFilename："+file.getOriginalFilename());
            System.out.println("getResource："+file.getResource());
            System.out.println("getInputStream："+file.getInputStream());
            System.out.println("getContentType："+file.getContentType());
            System.out.println("getSize："+file.getSize());
            System.out.println("------------------------");


            //保存到七牛云
            System.out.println("String fileName=qiniuService.saveImage(file);");
            String fileName=qiniuService.saveImage(file);
//            System.out.println("uploadImage-fileName:"+fileName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //添加新闻
    @PostMapping("/addNews")
    @ResponseBody
    public String addNews(@RequestBody News news){
        try {
            System.out.println("addNews begin");
            //传过来的news有title和link

            //news中的userid为当前登录用户的id
            news.setUserId(hostHolder.getUser().getId());

            //添加时间 【yyyy-MM-dd HH:mm】
            String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            news.setCreatedDate(timeStr1);

            //设置默认图片
            news.setImageId(1);

            news.setLikeCount(0);
            news.setCommentCount(0);

            //保存到数据库中
            System.out.println("newsService.save(news);");
            newsService.save(news);
            return ToutiaoUtil.getJSONString(0,"分享成功");
        }catch (Exception e){
            e.printStackTrace();
            return ToutiaoUtil.getJSONString(1,"分享失败");
        }

    }


    //【查看原文】跳转到news的指定url
    @GetMapping("/lookOriginal")
    public void lookOriginal(@RequestParam("id") int id,
                            HttpServletResponse response){
        try {
            //通过传过来的id找到本条news
            News news = newsService.getById(id);
            String link = news.getLink();//获取url

            //跳到本link
            response.sendRedirect(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取【评论】
    @GetMapping("/getComment/{newsId}")
    @ResponseBody
    public List<ViewObject> getComment(@PathVariable("newsId") int newsId){

        //根据newsid查到本条news的所有评论
        List<Comment> comments = commentService.listComments(newsId, EntityType.ENTITY_NEWS);
//        System.out.println("getComment-comments:"+comments);

        List<ViewObject> commentVOs=new ArrayList<>();

        for (Comment comment:comments){
            ViewObject vo=new ViewObject();

            vo.set("comment",comment);
            vo.set("user",userService.getById(comment.getUserId()));

            commentVOs.add(vo);
        }

        return commentVOs;
    }


    //添加【评论】comment
    @PostMapping("/addComment")
    @ResponseBody
    public String addComment(@RequestBody Comment comment,
                             @RequestParam("newsId")int newsId){
        try {
            //传递过来的comment只有newsid和content
            comment.setEntityId(newsId);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityType(EntityType.ENTITY_NEWS);
//            String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            comment.setCreatedDate(timeStr1);
            comment.setStatus(0);
            //将本条评论上传到数据库
//            System.out.println("NewsController-getComment-comment:"+comment);
            commentService.save(comment);

            //将评论数量 同步到 数据库news里的comment_count
            int commnetCount = commentService.countComment(comment.getEntityId(), comment.getEntityType());
//            System.out.println("NewsController-getComment-commnetCount:"+commnetCount);
            newsService.updateCommentCount(comment.getEntityId(),commnetCount);

            return ToutiaoUtil.getJSONString(0,"评论成功");
        }catch (Exception e){
            e.printStackTrace();
            return ToutiaoUtil.getJSONString(1,"评论失败");
        }

    }


}
