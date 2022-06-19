package angel.service.abandon;

import angel.domain.Image;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService extends IService<Image> {
    String saveImage(MultipartFile file) throws Exception;

    Image getLastImage();
}
