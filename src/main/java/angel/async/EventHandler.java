package angel.async;

import java.util.List;


public interface EventHandler {
    void doHandle(EventModel model);//要处理的model
    List<EventType> getSupportEventTypes();//要关注的eventType
}
