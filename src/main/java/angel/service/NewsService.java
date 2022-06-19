package angel.service;

import angel.domain.News;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NewsService extends IService<News> {

    List<News> listNewsByPageAndTitle( String title, int begin, int size);

    String getImageNameByNewsId(String newsId);

    int countNewsByTitle(String title);

    void updateLikeCount(int newsId,int likeCount);

    void updateCommentCount(int id,int commentCount);
}
