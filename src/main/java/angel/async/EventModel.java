package angel.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author daijizai
 * @version 1.0
 * @date 2022/5/20 20:19
 * @description 放在队列里面的 表示发生了生么事情的
 */
public class EventModel {
    private EventType type;//事件的类型
    private int actorId;//触发者
    private int entityId;//触发对象id【newsId】
    private int entityType;//触发对象的类型【新闻】
    private int entityOwnerId;//触发对象的拥有者【新闻作者】
    private Map<String, String> exts = new HashMap<String, String>();//触发现场的信息


    public EventModel() {

    }
    public EventModel(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }

    public String getExts(String key){
        return exts.get(key);
    }

    public EventModel setExts(String key,String value){
        exts.put(key,value);
        return this;
    }


}
