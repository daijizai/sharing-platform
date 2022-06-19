package angel.service.abandon;

import angel.dao.ImageDao;
import angel.dao.NewsDao;
import angel.domain.Image;
import angel.util.ToutiaoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

//@Service
public class ImageServiceImpl extends ServiceImpl<ImageDao , Image> implements ImageService {

    @Autowired
    ImageDao imageDao;

    @Autowired
    NewsDao newsDao;

    //储存到本地
    public String saveImage(MultipartFile file) throws Exception{

        //获取文件后缀名
        int dotPot = file.getOriginalFilename().lastIndexOf(".");
        if(dotPot<0){
            return null;
        }
        String fileExt=file.getOriginalFilename().substring(dotPot+1).toLowerCase();

        //判断文件是否符合图片的格式
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }

        //到此已经确定图片格式

        //给图片重命名
        String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;

        //保存到本地
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath());

        Image image = new Image();

        image.setName(fileName);
        image.setStatus(0);

        imageDao.insert(image);

        //用 刚加入的image的id 更新 最新的news
        Image lastImage = imageDao.getLastImage();
        int lastImageId = lastImage.getId();
        newsDao.updateImageId(lastImageId);

        //返回图片的url
        return fileName;

    }

    public Image getLastImage(){
        return imageDao.getLastImage();
    }
}
