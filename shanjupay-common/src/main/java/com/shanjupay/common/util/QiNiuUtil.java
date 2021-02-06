package com.shanjupay.common.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/*****
 *@Author NJL
 *@Description 七牛云
 */
public class QiNiuUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiNiuUtil.class);

    /***
     * 上传文件至七牛云方法
     * @param accessKey accessKey
     * @param secretKey secretKey
     * @param bucket 七牛云存储空间名称
     * @param fileName 文件名
     * @param uploadBytes 文件字节数组
     */
    public static void uploadToQiNiu(String accessKey,String secretKey,String bucket,String fileName, byte[] uploadBytes) throws RuntimeException {
        //构造一个带指定 Region 对象的配置类 设置存储区域
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        FileInputStream inputStream = null;
        //进行认证
        Auth auth = Auth.create(accessKey, secretKey);
        //认证成功返回的令牌
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(uploadBytes, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            LOGGER.info("上传成功 文件名:{}", putRet.key);
            LOGGER.info("上传成功 hash:{}", putRet.hash);
        } catch (Exception e) {
            LOGGER.info("上传失败 Exception:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void testUpload() {
        //构造一个带指定 Region 对象的配置类 设置存储区域
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "9SzzqXkEj2s7SeuviS_K652oaMGU8rMuWNwSDrUe";
        String secretKey = "TIYlItxSBzcZHEYjXfVRxfn4bVZBEwMwiDComm1S";
        String bucket = "shanjupay-jin";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID() + ".png";
        FileInputStream inputStream = null;
        try {
            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            String filePath = "C:\\Users\\21201\\Pictures\\Saved Pictures\\4.jpg";
            inputStream = new FileInputStream(new File(filePath));
            byte[] uploadBytes = IOUtils.toByteArray(inputStream);

            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(uploadBytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            //ignore
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        QiNiuUtil.testUpload();
    }

}
