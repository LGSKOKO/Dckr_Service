package com.esunny.dckr.util;

import java.util.HashMap;

/**
 * @author: 李先生
 * @description: 专属Kompose的映射表，前端字段->cmd参数
 * @Version 1.0
 * @create: 2021-02-14 11:30
 **/
public class KomposeMap {
    private static HashMap<String, String> map;

    //初始化map
    static {
        map = new HashMap<>(6);
        map.put("YAML", "");
        map.put("JSON", " -j");
        map.put("HELM", " -c");
        map.put("DEFAULT", "");
        map.put("Deployment", "");
        map.put("DaemonSet", " --controller daemonSet");
        map.put("ReplicaSet", " --controller replicationController");
    }


    /**
     * 提供统一方法，获取
     *
     * @param key 前端字段值
     * @return kompose的命令参数
     */
    public static String getValue(String key) {
        return map.get(key);
    }
}
