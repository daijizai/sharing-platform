package angel.service.impl;

import angel.dao.ImageDao;
import angel.dao.NewsDao;
import angel.domain.Image;
import angel.service.QiniuService;
import angel.util.ToutiaoUtil;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuServiceImpl implements QiniuService {

    @Autowired
    ImageDao imageDao;

    @Autowired
    NewsDao newsDao;

    //设置好账号的ACCESS_KEY和SECRET_KEY
    private static final String ACCESS_KEY = "hSzhVkZAfvUpak0luGIv8NAcKGCSp3ZcQS5OhDM7";
    private static final String SECRET_KEY = "PrwIY_MUaX7ItGN-KabCoxbBA9Z0wlw0eROdawLh";
    //要上传的空间
    private static final String BUCKET_NAME  = "toutiao-test-angel";

    //外链域名
//    private static final String QINIU_IMAGE_DOMAIN = "http://r65ofitrn.hd-bkt.clouddn.com";
    private static final String QINIU_IMAGE_DOMAIN = "http://daijizai.asia";

    //密钥配置
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

    //构造一个带指定Region对象的配置类
    private Configuration cfg = new Configuration(Region.region0());
    //...其他参数参考类注释
    private UploadManager uploadManager = new UploadManager(cfg);

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        return auth.uploadToken(BUCKET_NAME);
    }

    //从七牛云获取image
    public String getImage(String fileName){
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String publicUrl = String.format("%s/%s", QINIU_IMAGE_DOMAIN, fileName);
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        return finalUrl;
    }

    //保存到七牛云
    public String saveImage(MultipartFile file) throws IOException {
        try {
            //获取文件后缀名
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();

            //判断文件是否符合图片的格式
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }

            //给图片重命名
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            //调用put方法上传 保存到七牛云
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());

            Image image = new Image();
            image.setName(fileName);
            image.setStatus(0);
            imageDao.insert(image);


            //用 刚加入的image的id 更新 最新的news
            Image lastImage = imageDao.getLastImage();
            int lastImageId = lastImage.getId();
            System.out.println("lastImageId"+lastImageId);
            newsDao.updateImageId(lastImageId);

            if (res.isOK() && res.isJson()) {
                return JSONObject.parseObject(res.bodyString()).get("key").toString();
            } else {
                System.out.println(("七牛异常:" + res.bodyString()));
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            e.printStackTrace();
            return null;
        }
    }
}
