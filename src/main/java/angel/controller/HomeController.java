package angel.controller;

import angel.domain.HostHolder;
import angel.domain.News;
import angel.domain.User;
import angel.domain.ViewObject;
import angel.service.NewsService;
import angel.service.QiniuService;
import angel.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QiniuService qiniuService;


    //将查到的【news列表】和【条件title】送到这 封装为viewObject用于传输到页面
    //【viewObject包含news、user、totalCount】
    public List<ViewObject> getViewObject(List<News> newsList, String title){
        List<ViewObject> viewObjectList=new ArrayList<>();

        for(News news:newsList){
            ViewObject viewObject = new ViewObject();

            viewObject.set("news",news);

            User user = userService.getById(news.getUserId());
            user.setPassword("");//For safety
            user.setSalt("");//For safety
            viewObject.set("user",user);
            viewObject.set("totalCount",newsService.countNewsByTitle(title));//添加新闻总数目 为了分页

            //为了在微信小程序上在展示news的时候直接显示图片 需要将图片url之间传到前端
            String newsId = Integer.toString(news.getId());
            String image = qiniuService.getImage(newsService.getImageNameByNewsId(newsId));
            viewObject.set("imgUrl",image);


            viewObjectList.add(viewObject);
        }

        return viewObjectList;
    }

    //获取当前用户姓名
    @GetMapping("/user")
    public String getCurrentUsername(){
        try {
            //未登录状态时本行报错
            String userName=hostHolder.getUser().getName();

            return "用户名："+userName;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("未登录用户点击【用户】");

            return "请确认登录状态";
        }
    }

    //获取当前用户对象
    @GetMapping("/userObject")
    public User getCurrentUser(){
        try {
            //未登录状态时本行报错
            User user = hostHolder.getUser();
            user.setSalt("");
            user.setPassword("");

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("未登录用户点击【用户】");

            User user = new User();
            user.setName("请确认登录状态");
            return user;
        }
    }



    //根据title 分页 获取所有news
    @PostMapping("/newsT/{currentPage}/{pageSize}")
    public List<ViewObject> getAllNewsByPageAndTitle(@PathVariable("currentPage") int currentPage,
                                                     @PathVariable("pageSize") int pageSize,
                                                     @RequestBody  String JsonTitle){

        //json格式的字符串转json对象
        JSONObject jsonObjectTitle = JSONObject.parseObject(JsonTitle);
        String title = jsonObjectTitle.getString("title");//解析json对象

        //error:每页5个页数为2，搜索【大】 搜不到。与分页有关
//        if(title!=null){
//            currentPage=1;
//        }

        List<News> newsList = newsService.listNewsByPageAndTitle(title,currentPage,pageSize);

        return getViewObject(newsList,title);
    }

    //获取所有新闻数据
    @GetMapping("/news")
    public List<News> getAllNews(){
        return newsService.list();
    }


}
