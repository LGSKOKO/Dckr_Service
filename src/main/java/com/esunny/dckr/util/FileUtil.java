package com.esunny.dckr.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author: 李先生
 * @description: 文件操作工具类
 * @Version 1.0
 * @create: 2021-02-24 21:59
 **/
public class FileUtil {

    /**
     * 通过文件获取存储路径
     *
     * @param uploadPath 上传路径
     * @param file       文件
     * @param suffixList 合法后缀列表
     * @return 文件存储路径
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getStoragePath(String uploadPath, MultipartFile file, List<String> suffixList) throws IOException, InterruptedException {

        //获取文件名
        String filename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = "";
        if (null != filename) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        //判断文件格式是否符合条件
        boolean isMatchSuffix = false;
        for (String str : suffixList) {
            if (str.equals(suffix)) {
                isMatchSuffix = true;
                break;
            }
        }

        if (isMatchSuffix) {
            //创建文件夹
            String dirPath = uploadPath + RandomStringUtils.randomAlphanumeric(16) + "/";
            mkdir(dirPath);
            Thread.sleep(500);
            //存储路径 storagePath = dirPath + filename;
            return dirPath + filename;
        }

        return "";
    }

    /**
     * 构建目录
     *
     * @param dirPath 目录路径
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    public static void mkdir(String dirPath) throws IOException, InterruptedException {
        File file=new File(dirPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 构建文件
     *
     * @param filePath    文件路径
     * @param content 文件内容
     * @throws IOException IO异常
     */
    public static void mkFile(String filePath, String content) throws IOException {
        BufferedWriter bfWriter = new BufferedWriter(new FileWriter(filePath));
        //写内容
        bfWriter.write(content);
        //刷新缓存
        bfWriter.flush();
        //关闭资源
        bfWriter.close();
    }

    /**
     * 根据路径判断文件是否存在
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean isFileExit(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 构建YAML文件
     *
     * @param dir 存储路径
     * @param fileName  文件名
     * @param jsonStr   JSON字符串
     * @throws IOException
     */
    public static void constructYaml(String dir, String fileName, String jsonStr) throws IOException {
        String filePath = dir + "/" + fileName;
        System.out.println("file:" + filePath);
        System.out.println(jsonStr);
        String yamlStr = JsonToYaml(jsonStr);
        mkFile(filePath, yamlStr);
    }

    /**
     * JSON字符串转YAML字符串
     *
     * @param jsonStr JSON字符串
     * @return yaml字符串
     */
    public static String JsonToYaml(String jsonStr) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = (Map<String, Object>) yaml.load(jsonStr);
        String yamlStr = yaml.dumpAsMap(map);
        System.out.println(yamlStr);
        return yamlStr;
    }
}
