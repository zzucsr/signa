package com.superSign.utils;

import java.io.*;

/**
 * @ClassName Shell
 * @Description TODO
 * @Author 陈思睿
 * @Date
 **/
public class Shell {

    /**
     * 是否执行成功
     * @param command
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean run(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        printMessage(process.getInputStream());
        printMessage(process.getErrorStream());
        //阻塞当前线程 当子线程执行结束
        int exitValue = process.waitFor();
        //当执行成功返回一般都是0
        return exitValue==0;
    }

    /**
     * 异步调用
     * @param input
     */
    private static void printMessage(final InputStream input) {
        new Thread(new Runnable() {
            public void run() {
                Reader reader = new InputStreamReader(input);
                BufferedReader bf = new BufferedReader(reader);
                String line = null;
                try {
                    while((line=bf.readLine())!=null)
                    {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
