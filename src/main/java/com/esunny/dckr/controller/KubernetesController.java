package com.esunny.dckr.controller;

import com.esunny.dckr.domain.dto.KubernetesUploadStructure;
import com.esunny.dckr.domain.dto.ReturnStructure;
import com.esunny.dckr.domain.dto.UploadStructure;
import com.esunny.dckr.service.KubernetesService;
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
import java.util.Map;

/**
 * @author: 李先生
 * @description: Kubernetes控制器
 * @Version 1.0
 * @create: 2021-02-13 21:56
 **/
@RestController
@RequestMapping("kubernetes")
@CrossOrigin
public class KubernetesController {
    @Resource
    private KubernetesService kubeService;

    /**
     * 上传kubernetes结构体
     *
     * @param uploadStructure 上传结构体
     * @return 返回结构体
     */
    @PostMapping("/upload/compose")
    @ResponseBody
    public ReturnStructure createKubeByCompose(UploadStructure uploadStructure) {

        if (uploadStructure == null) {
            return new ReturnStructure(400, "上传失败，服务器错误！！！");
        }

        Map<String, String> dataMap = new HashMap<>(4);

        try {
            //上传文件 并且 获取存储路径
            String storagePath = kubeService.upload(uploadStructure);
            Thread.sleep(500);
            //通过上传结构体和存储路径，将文件转为kubernetes文件
            kubeService.convertToKubernetes(uploadStructure, storagePath);
            Thread.sleep(1000);
            //删除源文件，并将kubernetes文件进行归档
            String targetPath = kubeService.putInOrder(storagePath);

            dataMap.put("targetPath", targetPath);

        } catch (ServiceException | IOException | InterruptedException e) {
            return new ReturnStructure(400, "构建失败！");
        }

        return new ReturnStructure(200, "构建成功！", dataMap);

    }

    @PostMapping("/guide")
    @ResponseBody
    public ReturnStructure createKubeByGuide(@RequestBody KubernetesUploadStructure kubeMap) {

        if (kubeMap == null) {
            return new ReturnStructure(400, "上传失败，服务器错误！！！");
        }

        Map<String, String> dataMap = new HashMap<>(4);
        try {
            System.out.println(kubeMap);
            //删除源文件，并将kubernetes文件进行归档
            String targetPath = kubeService.createK8sZip(kubeMap);

            dataMap.put("targetPath", targetPath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ReturnStructure(400, "构建k8s压缩包失败");
        }

        return new ReturnStructure(200, "构建成功！", dataMap);
    }

    /**
     * @param dir      16位随机字符串目录
     * @param response HttpServletResponse
     * @return 统一返回结构体
     */
    @GetMapping("download/{dir}")
    public ReturnStructure download(@PathVariable("dir") String dir, HttpServletResponse response) {

        if (StringUtils.isBlank(dir)) {
            return new ReturnStructure(400, "文件不存在，下载失败！！！");
        }

        String targetPath = "/dckr/kubernetes/" + dir + "/kubernetes.tar";

        try {
            //application/octet-stream
            response.setHeader("content-type", "application/x-tar");
            response.setContentType("application/x-tar");
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode("kubernetes.tar", "UTF-8"));
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
            return new ReturnStructure(500, "服务器错误，下载失败！！！");
        }
    }


}
