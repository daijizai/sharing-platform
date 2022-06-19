package angel.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public class ToutiaoUtil {

    public static final String TOUTIAO_DOMAIN="http://127.0.0.1/";
    public static final String IMAGE_DIR="e:/upload/";
    public static final String[] IMAGE_FILE_EXT=new String[]{"png","bmp","jpg","jpeg"};

    public static boolean isFileAllowed(String fileExt){
        for (String ext:IMAGE_FILE_EXT){
            if(ext.equals(fileExt)){
                return true;
            }
        }

        return false;
    }


    //封装json
    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        for(Map.Entry<String,Object> entry:map.entrySet()){
            json.put(entry.getKey(),entry.getValue());
        }
        return json.toJSONString();
    }


}
