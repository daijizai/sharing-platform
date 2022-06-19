package angel.service.impl;

import angel.dao.ImageDao;
import angel.dao.NewsDao;
import angel.domain.Image;
import angel.domain.News;
import angel.service.NewsService;
import angel.util.ToutiaoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsDao, News> implements NewsService {

    @Autowired
    NewsDao newsDao;

    @Autowired
    ImageDao imageDao;

    public List<News> listNewsByPageAndTitle( String title,
                                              int currentPage,
                                              int pageSize){
        //计算开始索引&查询条目数
        int begin=(currentPage-1)*pageSize;
        int size=pageSize;
        return newsDao.listNewsByPageAndTitle(title,begin,size);
    }

    public String getImageNameByNewsId(String newsId){
        //根据newsid获取imageid
        News news = newsDao.selectById(newsId);
        int imageId = news.getImageId();

        //根据imageid获取image的name
        Image image = imageDao.selectById(imageId);
        String filename = image.getName();

        return filename;
    }

    public int countNewsByTitle(String title){
        return newsDao.countNewsByTitle(title);
    }

    public void updateLikeCount(int newsId,int likeCount){
        newsDao.updateLikeCount(newsId,likeCount);
    }

    public void updateCommentCount(int id,int commentCount){
        newsDao.updateCommentCount(id,commentCount);
    }
}
