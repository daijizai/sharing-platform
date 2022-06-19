package angel.controller;

import angel.domain.*;
import angel.service.MessageService;
import angel.service.UserService;
import angel.util.ToutiaoUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    //根据传过来的toUserName获取toUserId
    @Autowired
    UserService userService;

    //添加私信 传过来：私信内容、收信人id
    @PostMapping("/addMessage")
    @ResponseBody
    public String addMessage(@RequestBody String contentAndToUserName){
        try {

            //接收到content和tousername的json字符串
            JSONObject jsonObject = JSONObject.parseObject(contentAndToUserName);//转换为json对象 方便解析
            String content = jsonObject.getString("content");//根据key得到value
            String toUserName = jsonObject.getString("toUserName");

            Message message = new Message();
            message.setContent(content);

            User toUser=userService.getUserByName(toUserName);
            message.setToId(toUser.getId());

            message.setFromId(hostHolder.getUser().getId());
            String timeStr1= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            message.setCreatedDate(timeStr1);

            int fromId=hostHolder.getUser().getId();
            int toId=message.getToId();
            message.setConversationId(fromId<toId
                    ? String.format("%d_%d",fromId,toId)
                    : String.format("%d_%d",toId,fromId));

//            System.out.println("addMessage-message:"+message);

            messageService.save(message);
            return ToutiaoUtil.getJSONString(0,"发送成功");

        } catch (Exception e){
            e.printStackTrace();
            return ToutiaoUtil.getJSONString(1,"发送失败");

        }

    }


    //获取当前登录用户的私信
    @GetMapping("/getMessage")
    @ResponseBody
    public List<ViewObject> getMessage(){

        try {
            int currentUserId = hostHolder.getUser().getId();
//        System.out.println("getMessage-currentUserId:"+currentUserId);
            List<Message> messages = messageService.getMessageByToId(currentUserId);

            List<ViewObject> messageVOs=new ArrayList<>();


            for (Message message:messages){
                ViewObject vo=new ViewObject();


                vo.set("message",message);
                vo.set("user",userService.getById(message.getFromId()));


                messageVOs.add(vo);
            }

            return messageVOs;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("【私信】获取出现问题");
            return null;
        }

    }

}
