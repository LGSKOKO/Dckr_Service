package com.esunny.dckr.controller;

import com.esunny.dckr.domain.dto.RancherUploadStructure;
import com.esunny.dckr.domain.dto.ReturnStructure;
import com.esunny.dckr.domain.dto.UploadStructure;
import com.esunny.dckr.service.KubernetesService;
import com.esunny.dckr.service.RancherService;
import com.esunny.dckr.util.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: 李先生
 * @description: Rancher控制器
 * @Version 1.0
 * @create: 2021-02-13 21:57
 **/
@RestController
@RequestMapping("rancher")
@CrossOrigin
public class RancherController {
    @Resource
    private KubernetesService kubeService;
    @Resource
    private RancherService rancherService;

    /**
     * DockerCompose 上传文件
     *
     * @param uploadStructure 上传结构体
     * @return 返回结构体
     */
    @PostMapping("upload/compose")
    @ResponseBody
    public ReturnStructure uploadByDockerCompose(UploadStructure uploadStructure) {

        if (uploadStructure == null) {
            return new ReturnStructure(400, "上传失败，服务器错误！！！");
        }

        Map<String, List<String>> dataMap = new HashMap<>(4);
        //先构建kubernetes结构，其次读取YAML文件转JSON并返回
        try {
            //上传文件 并且 获取存储路径
            String storagePath = kubeService.upload(uploadStructure);
            Thread.sleep(500);
            //通过上传结构体和存储路径，将文件转为kubernetes文件
            kubeService.convertToKubernetes(uploadStructure, storagePath);
            Thread.sleep(500);
            //删除源文件，并将kubernetes文件进行归档
            String targetPath = kubeService.putInOrder(storagePath);
            Thread.sleep(300);
            Map<String, List<String>> map = rancherService.parseAllFile(targetPath);
            List<String> targetList = new LinkedList<>();
            targetList.add(targetPath);

            dataMap.put("targetPath", targetList);
            dataMap.put("JSONList", map.get("JSONList"));
            dataMap.put("fileList", map.get("fileList"));

        } catch (ServiceException | IOException | InterruptedException e) {
            return new ReturnStructure(400, "上传失败！");
        }

        return new ReturnStructure(200, "上传成功！", dataMap);
    }

    /**
     * Kubernetes 上传文件
     *
     * @param uploadStructure 上传结构体
     * @return 返回结构体
     */
    @PostMapping("upload/kubernetes")
    @ResponseBody
    public ReturnStructure uploadByKubernetes(UploadStructure uploadStructure) {

        if (uploadStructure == null) {
            return new ReturnStructure(400, "上传失败，服务器错误！！！");
        }

        Map<String, List<String>> dataMap = new HashMap<>(4);

        try {
            //上传文件 并且 获取存储路径
            String storagePath = rancherService.upload(uploadStructure);
            Thread.sleep(500);
            //解压压缩包并解析所有文件，返回JSON列表
            Map<String, List<String>> map = rancherService.parseAllFile(storagePath);
            Thread.sleep(500);


            List<String> targetList = new LinkedList<>();
            targetList.add(storagePath);

            dataMap.put("targetPath", targetList);
            dataMap.put("JSONList", map.get("JSONList"));
            dataMap.put("fileList", map.get("fileList"));


        } catch (ServiceException | IOException | InterruptedException e) {
            return new ReturnStructure(400, "上传失败！");
        }

        return new ReturnStructure(200, "上传成功！", dataMap);

    }

    /**
     * 构建rancher文件
     *
     * @param rancherUpload rancher结构体
     * @return ReturnStructure
     */
    @PostMapping("/guide")
    @ResponseBody
    public ReturnStructure constructRancher(@RequestBody RancherUploadStructure rancherUpload) {

        if (rancherUpload == null) {
            return new ReturnStructure(400, "上传失败，服务器错误！！！");
        }
        try {
            rancherService.constructRancher(rancherUpload);
        } catch (IOException e) {
            return new ReturnStructure(400, "构建失败！");
        }
        //
        Map<String, String> dataMap = new HashMap<>(4);
        dataMap.put("downloadPath", rancherUpload.getStorePath());
        return new ReturnStructure(200,"构建成功",dataMap);
    }

    @GetMapping("download/{dir}")
    public ReturnStructure download(@PathVariable("dir") String dir, HttpServletResponse response) {

        if (StringUtils.isBlank(dir)) {
            return new ReturnStructure(400, "下载失败！！！");
        }

        //对文件进行打包处理 并获取 目标路径
        String targetPath;
        try {
            targetPath = rancherService.putInOrder(dir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ReturnStructure(400, "下载失败！！！");
        }

        //返回压缩文件路径
        try {
            //application/octet-stream
            response.setHeader("content-type", "application/x-tar");
            response.setContentType("application/x-tar");
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode("rancher.tar", "UTF-8"));
            FileInputStream fis = new FileInputStream(new File(targetPath));
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024 * 5];
            int b;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
            os.close();
            fis.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return new ReturnStructure(400, "下载失败！！！");
        }

    }
}
