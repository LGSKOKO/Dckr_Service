package com.esunny.dckr.service;

import com.alibaba.fastjson.JSON;
import com.esunny.dckr.domain.dto.RancherUploadStructure;
import com.esunny.dckr.domain.dto.UploadStructure;
import com.esunny.dckr.util.CmdUtil;
import com.esunny.dckr.util.FileUtil;
import com.esunny.dckr.util.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: 李先生
 * @description: Rancher服务类
 * @Version 1.0
 * @create: 2021-02-13 22:01
 **/
@Service("rancherService")
public class RancherService {

    @Value("${dckr.rancher.path.upload}")
    private String uploadPath;

    /**
     * 基本路径前缀
     */
    private String basePath;
    /**
     * 文件名列表
     */
    private String[] fileList;

    /**
     * 用于暂存recursiveFindFile的结果。
     */
    private Map<String, Object> findFileMap = new HashMap<>(16);


    /**
     * 上传文件
     *
     * @param uploadStructure 上传结构体
     * @throws ServiceException 自定义Service层异常
     */
    public String upload(UploadStructure uploadStructure) throws ServiceException, IOException, InterruptedException {

        List<String> suffixList = new LinkedList<>();
        suffixList.add(".tar");
        suffixList.add(".zip");
        suffixList.add(".rar");
        //创建文件夹，获取文件存储路径
        String storagePath = FileUtil.getStoragePath(uploadPath, uploadStructure.getFile(), suffixList);

        //        String storagePath = getStoragePath(uploadStructure.getFile());

        if (StringUtils.isBlank(storagePath)) {
            throw new ServiceException("文件名后缀错误！！！");
        }

        try {
            uploadStructure.getFile().transferTo(new File(storagePath));
        } catch (IOException e) {
            throw new ServiceException("存储文件失败！！！");
        }
        System.out.println("存储成功！");
        return storagePath;
    }

    /**
     * 解析压缩包内所有文件，并返回JSON列表
     *
     * @param zipPath 目标压缩包的路径
     * @return JSON列表
     * @throws IOException
     * @throws InterruptedException
     */
    public Map<String, List<String>> parseAllFile(String zipPath) throws IOException, InterruptedException {
        //解压文件，并寻找目标文件
        String filePath = unzip(zipPath);
        recursiveFindFile(filePath);
        String basePath = (String) this.findFileMap.get("basePath");
        String[] fileNames = (String[]) this.findFileMap.get("fileNames");

//        System.out.println("filePath：" + filePath);
//        for (String s : fileNames) {
//            System.out.println("文件名：" + s);
//        }
//        System.out.println("files:" + fileNames);
        return yamlToJson(basePath, fileNames);
    }

    /**
     * 对上传文件进行解压处理 并删除源压缩包
     *
     * @param zipPath 文件路径
     * @return 解压后的文件路径
     */
    private String unzip(String zipPath) throws IOException, InterruptedException {
        //获取存储目录路径，以及后缀名
        String basePath = zipPath.substring(0, zipPath.lastIndexOf("/"));
        String suffix = zipPath.substring(zipPath.lastIndexOf("."));
//        System.out.println("suffix:" + suffix);
        StringBuffer command = new StringBuffer();
        switch (suffix) {
            case ".rar":
                command.append("unrar x ");
                command.append(zipPath);
                command.append("  ");
                command.append(basePath);
                break;
            case ".zip":
                command.append("unzip -o ");
                command.append(zipPath);
                command.append(" -d ");
                command.append(basePath);
                break;
            case ".tar":
                command.append("tar -xvf ");
                command.append(zipPath);
                command.append(" -C ");
                command.append(basePath);
                break;
            default:
                break;
        }
        CmdUtil.executeCmd(command);

        //删除源压缩包
        command = new StringBuffer();
        command.append("rm -rf ");
        command.append(zipPath);
        CmdUtil.executeCmd(command);

        return basePath;
    }

    /**
     * 递归寻找 目标文件，即template下的所有文件
     *
     * @param filePath 初始文件路径
     * @return Map
     */
    private void recursiveFindFile(String filePath) {
        File file = new File(filePath);
//        System.out.println("isDirectory:" + file.isDirectory());
        if (file.isDirectory()) {
            String[] files = file.list();
//            System.out.println(filePath + "的fileList:" + files);
            //递归终止条件
            boolean isTargetPath = filePath.contains("templates");
            if (isTargetPath) {
                System.out.println("find targetPath");
                this.findFileMap.put("basePath", filePath);
                this.findFileMap.put("fileNames", files);
                return;
            }

            if(null == files || files.length <=0){return;}
            for (String str : files) {
                String newFilePath = filePath + "/" + str;
                recursiveFindFile(newFilePath);
            }
        }

    }


    /**
     * 读取所有目标YAML文件 解析为JSON，存入列表。
     *
     * @param basePath 基本路径前缀
     * @param files    文件名
     * @return Map JSON列表,文件列表
     * @throws FileNotFoundException
     */
    private Map<String, List<String>> yamlToJson(String basePath, String[] files) throws FileNotFoundException {

        Yaml yaml = new Yaml();
        LinkedList<String> JSONList = new LinkedList<>();
        LinkedList<String> fileList = new LinkedList<>();
        for (String fileName : files) {
            //过滤非法文件
            if (!fileName.contains("yaml") && !fileName.contains("yml")) {
                continue;
            }
            String filePath = basePath + "/" + fileName;
            System.out.println(filePath);
            File file = new File(filePath);
            //读入文件
            Object result = yaml.load(new FileInputStream(file));
            JSONList.add(JSON.toJSONString(result));
            fileList.add(fileName);
        }

        Map<String, List<String>> map = new HashMap<>(4);
        map.put("JSONList", JSONList);
        map.put("fileList", fileList);
        return map;
    }

    /**
     * 构建Rancher
     *
     * @param rancherUpload rancherUpload数据结构
     */
    public void constructRancher(RancherUploadStructure rancherUpload) throws IOException {
        //获取rancherUpload里的各个属性值。
        String storePath = rancherUpload.getStorePath();
        String valueJson = rancherUpload.getValueJson();
        String questionJson = rancherUpload.getQuestionJson();
        String[] fileNames = rancherUpload.getFileNameArray();
        String[] fileJsons = rancherUpload.getFileJsonArray();

        //通过storePath，调用系列方法获取，valuePath、questionPath、templatesPath。
        //storePath是xxx/kubernetes.tar等,我们只需要xxx/
        String filePath = storePath.substring(0, storePath.lastIndexOf("/"));
        recursiveFindFile(filePath);
        // templatesPath格式，xxx/templates/
        String templatesPath = (String) this.findFileMap.get("basePath");
        // valuePath、questionPath格式：xxx/，即去掉templates/
        String valuePath = templatesPath.replace("/templates", "");
        String questionPath = valuePath;

//        System.out.println("valuesPath:" + valuePath);
//        System.out.println("QuestionsPath:" + questionPath);

        //构建相应的YAML文件
        FileUtil.constructYaml(valuePath, "values.yaml", valueJson);
        FileUtil.constructYaml(questionPath, "questions.yaml", questionJson);
        for (int index = 0; index < fileNames.length; index++) {
            String fileName = fileNames[index];
            String fileJson = fileJsons[index];
            FileUtil.constructYaml(templatesPath, fileName, fileJson);
        }

    }


    /**
     * 调用Linux系统 进行文件归档
     *
     * @param dir 文件存储路径的 随机数部分
     * @return 目标文件
     * @throws IOException 异常
     */
    public String putInOrder(String dir) throws IOException, InterruptedException {

        //构建相应路径和压缩包名
        String storagePath = "/dckr/kubernetes/" + dir + "/";
        String zipName = "rancher.tar";
        boolean isTruePath = FileUtil.isFileExit(storagePath);

        if (!isTruePath) {
            storagePath = "/dckr/rancher/" + dir + "/";
        }

        //进入相应目录,并进行打包
        List<String> commands = new LinkedList<>();
        StringBuffer command = new StringBuffer();

        command = new StringBuffer("cd ");
        command.append(storagePath);
        commands.add(command.toString());

        command = new StringBuffer("tar -cvf ");
        command.append(zipName);
        command.append(" .");
        commands.add(command.toString());

        CmdUtil.executeCmd(commands);

        //        System.out.println("rancherTargetPath：" + storagePath + zipName);
        // targetPath = storagePath + zipName;
        return storagePath + zipName;
    }


}
