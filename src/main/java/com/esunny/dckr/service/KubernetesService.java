package com.esunny.dckr.service;

import com.esunny.dckr.domain.dto.KubernetesUploadStructure;
import com.esunny.dckr.domain.dto.UploadStructure;
import com.esunny.dckr.util.CmdUtil;
import com.esunny.dckr.util.FileUtil;
import com.esunny.dckr.util.KomposeMap;
import com.esunny.dckr.util.ServiceException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: 李先生
 * @description: Kubernetes服务类
 * @Version 1.0
 * @create: 2021-02-13 22:00
 **/
@Service("kubernetesService")
public class KubernetesService {

    @Value("${dckr.kubernetes.path.upload}")
    private String uploadPath;

    /**
     * @param kubeMap KubernetesUploadStructure
     * @throws IOException IO异常
     * @throws InterruptedException 中断异常
     */
    public String createK8sZip(KubernetesUploadStructure kubeMap) throws IOException, InterruptedException {
        //存储路径
        String dir = uploadPath + RandomStringUtils.randomAlphanumeric(16);
        //创建文件夹
        FileUtil.mkdir(dir);

        String[] serviceNames = kubeMap.getServiceName();
        String[] serviceJsons = kubeMap.getServiceJson();
        this.createK8sFile(dir, serviceNames, serviceJsons);
//        System.out.println("service:"+serviceNames.length+","+serviceJsons.length);

        String[] secretNames = kubeMap.getSecretName();
        String[] secretJsons = kubeMap.getSecretJson();
        this.createK8sFile(dir, secretNames, secretJsons);
        //        System.out.println("secrets:"+secretNames.length+","+secretJsons.length);



        String[] configMapNames = kubeMap.getConfigMapName();
        String[] configMapJsons = kubeMap.getConfigMapJson();
        this.createK8sFile(dir, configMapNames, configMapJsons);
        //        System.out.println("configMap:"+configMapNames.length+","+configMapJsons.length);



        String[] podNames = kubeMap.getPodName();
        String[] podJsons = kubeMap.getPodJson();
        this.createK8sFile(dir, podNames, podJsons);
//        System.out.println("pod:"+podNames.length+","+podJsons.length);


        String[] replicaSetNames = kubeMap.getReplicaSetName();
        String[] replicaSetJsons = kubeMap.getReplicaSetJson();
        this.createK8sFile(dir, replicaSetNames, replicaSetJsons);
//        System.out.println("replicaSe:"+replicaSetNames.length+","+replicaSetJsons.length);


        String[] deploymentNames = kubeMap.getDeploymentName();
        String[] deploymentJsons = kubeMap.getDeploymentJson();
        this.createK8sFile(dir, deploymentNames, deploymentJsons);
//        System.out.println("deployment:"+deploymentNames.length+","+deploymentJsons.length);


        //将storePath目录下文件归档
        this.putFileToK8sTar(dir);
        return dir;
    }

    /**
     * 创建k8s文件
     *
     * @param dir 存储路径
     * @param fileNames 文件名数组
     * @param jsons     JSON数组
     * @throws IOException IO异常
     */
    private synchronized void createK8sFile(String dir, String[] fileNames, String[] jsons) throws IOException {
        for (int index = 0; index < fileNames.length; index++) {
            String filename = fileNames[index] + ".yaml";
            String json = jsons[index];
            FileUtil.constructYaml(dir, filename, json);
        }
    }

    /**
     * 上传文件
     *
     * @param uploadStructure 上传结构体
     * @throws ServiceException 自定义Service层异常
     */
    public String upload(UploadStructure uploadStructure) throws ServiceException, IOException, InterruptedException {

        List<String> suffixList = new LinkedList<>();
        suffixList.add(".yaml");
        suffixList.add(".yml");
        //创建文件夹，获取文件存储路径
        String storagePath = FileUtil.getStoragePath(uploadPath, uploadStructure.getFile(), suffixList);

        if (StringUtils.isBlank(storagePath)) {
            throw new ServiceException("文件名后缀错误！！！");
        }

//        System.out.println(storagePath);
        try {
            uploadStructure.getFile().transferTo(new File(storagePath));
        } catch (IOException e) {
            throw new ServiceException("存储文件失败！！！");
        }

        return storagePath;
    }


    /**
     * 对uploadStructure进行转换,调用Linux系统 执行kompose命令生成k8s文件
     *
     * @param uploadStructure 上传结构体
     * @param storagePath     文件存储路径
     * @throws IOException 异常
     */
    public void convertToKubernetes(UploadStructure uploadStructure, String storagePath) throws IOException, InterruptedException {
        //命令集合
        LinkedList<String> commands = new LinkedList<>();

        //创建一条 cd至存储目录 的命令
        //通过 存储路径获取存储目录
        int index = storagePath.lastIndexOf("/");
        String dir = storagePath.substring(0, index + 1);
        //创建 cmd命令
        StringBuffer command = new StringBuffer("cd ");
        command.append(dir);
        //添加至命令集合
        commands.add(command.toString());


        //创建一条 kompose convert 的命令
        command = new StringBuffer("kompose convert");
        command.append(KomposeMap.getValue(uploadStructure.getFormat()));
        command.append(KomposeMap.getValue(uploadStructure.getOrganization()));
        command.append(KomposeMap.getValue(uploadStructure.getTarget()));
        command.append(" --file ");
        command.append(storagePath);
        commands.add(command.toString());

        CmdUtil.executeCmd(commands);
    }


    /**
     * 调用Linux系统 删除源文件 并 进行文件归档
     *
     * @param storagePath 文件存储路径
     * @return 目标文件
     * @throws IOException 异常
     */
    public String putInOrder(String storagePath) throws IOException, InterruptedException {
        //删除源文件，即上传的文件
        StringBuffer command = new StringBuffer("rm -rf ");
        command.append(storagePath);
//        System.out.println("rm：" + command.toString());
        CmdUtil.executeCmd(command);

        //通过 存储路径获取存储目录
        int index = storagePath.lastIndexOf("/");
        String dir = storagePath.substring(0, index + 1);

        //将特定目录下的文件打包成 kubernetes.tar
        this.putFileToK8sTar(dir);

        // targetPath = dir + "kubernetes.tar ";
//        System.out.println("dir：" + dir + "kubernetes.tar ");
        return dir + "kubernetes.tar ";
    }


    /**
     * 将特定目录下的文件打包成 kubernetes.tar
     *
     * @param dir 存放文件的目录
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    private void putFileToK8sTar(String dir) throws IOException, InterruptedException {
        StringBuffer command = new StringBuffer();
        //依据存储目录 进行文件归档
        List<String> commands = new LinkedList<>();

        command = new StringBuffer("cd ");
        command.append(dir);
        commands.add(command.toString());

        command = new StringBuffer("tar -cvf kubernetes.tar .");
        commands.add(command.toString());

        CmdUtil.executeCmd(commands);
    }


}
