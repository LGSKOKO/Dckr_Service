package com.esunny.dckr.domain.dto;

import java.util.Arrays;

/**
 * @author: 李先生
 * @description: Rancher信息上传结构体
 * @Version 1.0
 * @create: 2021-03-03 18:34
 **/
public class RancherUploadStructure {
    /**
     * 文件存储路径
     */
    private String storePath;
    /**
     * 文件名数组
     */
    private String[] fileNameArray;
    /**
     * 文件JSON数组
     */
    private String[] fileJsonArray;
    /**
     * value.yaml的JSON字符串
     */
    private String valueJson;
    /**
     * question.yaml的JSON字符串
     */
    private String questionJson;

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String[] getFileNameArray() {
        return fileNameArray;
    }

    public void setFileNameArray(String[] fileNameArray) {
        this.fileNameArray = fileNameArray;
    }

    public String[] getFileJsonArray() {
        return fileJsonArray;
    }

    public void setFileJsonArray(String[] fileJsonArray) {
        this.fileJsonArray = fileJsonArray;
    }

    public String getValueJson() {
        return valueJson;
    }

    public void setValueJson(String valueJson) {
        this.valueJson = valueJson;
    }

    public String getQuestionJson() {
        return questionJson;
    }

    public void setQuestionJson(String questionJson) {
        this.questionJson = questionJson;
    }

    @Override
    public String toString() {
        return "RancherUploadStructure{" +
                "storePath='" + storePath + '\'' +
                ", fileNameArray=" + Arrays.toString(fileNameArray) +
                ", fileJsonArray=" + Arrays.toString(fileJsonArray) +
                ", valueJson='" + valueJson + '\'' +
                ", questionJson='" + questionJson + '\'' +
                '}';
    }
}
