package com.esunny.dckr.domain.dto;

import java.util.Arrays;

/**
 * @author: 李先生
 * @description: Kubernetes信息上传结构体
 * @Version 1.0
 * @create: 2021-03-25 22:21
 **/
public class KubernetesUploadStructure {

    private String[] secretName;
    private String[] configMapName;
    private String[] serviceName;
    private String[] podName;
    private String[] deploymentName;
    private String[] replicaSetName;
    private String[] secretJson;
    private String[] configMapJson;
    private String[] serviceJson;
    private String[] podJson;
    private String[] deploymentJson;
    private String[] replicaSetJson;

    public String[] getSecretName() {
        return secretName;
    }

    public void setSecretName(String[] secretName) {
        this.secretName = secretName;
    }

    public String[] getConfigMapName() {
        return configMapName;
    }

    public void setConfigMapName(String[] configMapName) {
        this.configMapName = configMapName;
    }

    public String[] getServiceName() {
        return serviceName;
    }

    public void setServiceName(String[] serviceName) {
        this.serviceName = serviceName;
    }

    public String[] getPodName() {
        return podName;
    }

    public void setPodName(String[] podName) {
        this.podName = podName;
    }

    public String[] getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String[] deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String[] getReplicaSetName() {
        return replicaSetName;
    }

    public void setReplicaSetName(String[] replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    public String[] getSecretJson() {
        return secretJson;
    }

    public void setSecretJson(String[] secretJson) {
        this.secretJson = secretJson;
    }

    public String[] getConfigMapJson() {
        return configMapJson;
    }

    public void setConfigMapJson(String[] configMapJson) {
        this.configMapJson = configMapJson;
    }

    public String[] getServiceJson() {
        return serviceJson;
    }

    public void setServiceJson(String[] serviceJson) {
        this.serviceJson = serviceJson;
    }

    public String[] getPodJson() {
        return podJson;
    }

    public void setPodJson(String[] podJson) {
        this.podJson = podJson;
    }

    public String[] getDeploymentJson() {
        return deploymentJson;
    }

    public void setDeploymentJson(String[] deploymentJson) {
        this.deploymentJson = deploymentJson;
    }

    public String[] getReplicaSetJson() {
        return replicaSetJson;
    }

    public void setReplicaSetJson(String[] replicaSetJson) {
        this.replicaSetJson = replicaSetJson;
    }

    @Override
    public String toString() {
        return "KubernetesUploadStructure{" +
                "secretName=" + Arrays.toString(secretName) +
                ", configMapName=" + Arrays.toString(configMapName) +
                ", serviceName=" + Arrays.toString(serviceName) +
                ", podName=" + Arrays.toString(podName) +
                ", deploymentName=" + Arrays.toString(deploymentName) +
                ", replicaSetName=" + Arrays.toString(replicaSetName) +
                ", secretJson=" + Arrays.toString(secretJson) +
                ", configMapJson=" + Arrays.toString(configMapJson) +
                ", serviceJson=" + Arrays.toString(serviceJson) +
                ", podJson=" + Arrays.toString(podJson) +
                ", deploymentJson=" + Arrays.toString(deploymentJson) +
                ", replicaSetJson=" + Arrays.toString(replicaSetJson) +
                '}';
    }
}
