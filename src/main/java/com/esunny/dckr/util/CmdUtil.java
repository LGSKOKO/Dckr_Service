package com.esunny.dckr.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author: 李先生
 * @description: 用于执行Linux命令的工具类
 * @Version 1.0
 * @create: 2021-02-15 19:20
 **/
public class CmdUtil {
    /**
     * Linux cmd命令执行的方法，一次性命令
     *
     * @param command linux命令
     * @throws IOException 异常
     */
    public static void executeCmd(StringBuffer command) throws IOException, InterruptedException {
        String[] cmd = {"/bin/sh", "-c", command.toString()};
        Process process = Runtime.getRuntime().exec(cmd);
        process.waitFor();
        process.destroy();
    }

    /**
     * Linux cmd命令执行的方法,长连接 多条命令。
     *
     * @param commands 多条command
     * @throws IOException          IO异常
     * @throws InterruptedException 中断异常
     */
    public static void executeCmd(List<String> commands) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("/bin/bash", null, null);

        //创建 输出流，即程序 输出 linux系统
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())), true);
        for (String command : commands) {
            out.println(command);
        }
        out.println("exit");

        // 方法阻塞, 等待命令执行完成（成功会返回0）
        process.waitFor();
        out.close();
        process.destroy();
    }
}
