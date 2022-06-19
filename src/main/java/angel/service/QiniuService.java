package angel.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface QiniuService {

    String saveImage(MultipartFile file) throws IOException;

    String getImage(String filename);

    String getUpToken();

}
