package angel.service;

public interface LikeService {

    int getLikeStatus(int userId,int entityType,int entityId);

    long like(int userId,int entityType,int entityId);

    long dislike(int userId,int entityType,int entityId);
}
