package com.esunny.dckr.domain.dto;

import org.springframework.web.multipart.MultipartFile;


/**
 * @author: 李先生
 * @description: 上传结构体，对应Kubernetes模块的基于Compose的向导式构建的上传结构体
 * @Version 1.0
 * @create: 2021-02-13 16:14
 **/
public class UploadStructure {

    /**
     * 文件格式
     */
    private String format;
    /**
     * 组织形式
     */
    private String organization;
    /**
     * 目标对象
     */
    private String target;
    /**
     * 上传文件
     */
    private MultipartFile file;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "UploadStructure{" +
                "format='" + format + '\'' +
                ", organization='" + organization + '\'' +
                ", target='" + target + '\'' +
                ", file=" + file +
                '}';
    }
}
