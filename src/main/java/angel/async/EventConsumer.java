package angel.async;

import angel.util.JedisAdapter;
import angel.util.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
当一个类实现这个接口之后，Spring启动后，初始化Bean时，若该Bean实现InitialzingBean接口，
会自动调用afterPropertiesSet()方法，完成一些用户自定义的初始化操作。
 */

/*
当一个类实现了这个接口之后，这个类就可以方便的获得ApplicationContext对象（spring上下文），
Spring发现某个Bean实现了ApplicationContextAware接口，Spring容器会在创建该Bean之后，
自动调用该Bean的setApplicationContext（参数）方法，调用该方法时，
会将容器本身ApplicationContext对象作为参数传递给该方法。
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");

        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);//把所有继承了EventHandler.class的类都找出来

        //打印 仅供查看使用
        for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
            String key = entry.getKey();
            System.out.println("key:"+key);
        }


        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();//看handle要哪个事件

                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {//如果config中没有这个type
                        config.put(type, new ArrayList<>());//放进去
                    }

                    // 注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        }

        // 启动线程去消费事件
        Thread thread = new Thread(() -> {
            while (true) {
                String key = RedisKeyUtil.getEventQueueKey();
                List<String> messages = jedisAdapter.brpop(0, key);

                EventModel eventModel = JSON.parseObject(messages.get(1), EventModel.class);

                // 找到这个事件的处理handler列表
                if (!config.containsKey(eventModel.getType())) {
                    logger.error("不能识别的事件");
                    continue;
                }

                for (EventHandler handler : config.get(eventModel.getType())) {
                    handler.doHandle(eventModel);//通过事件处理器处理事件
                }

            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext");
        this.applicationContext = applicationContext;
    }
}

