// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑, 永无BUG!
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 * Summary: 程序入口
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/3
 * time   : 22:24
 */
public class SyncMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMain.class);

    public static final Object forWait = new Object();

    public static void main(String[] args)
            throws InterruptedException, IOException {
        // 获取jvm名
        String vm = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isBlank(vm)) {
            LOGGER.error("can't get pid");
            return;
        }
        FileOutputStream out = null;
        try {
            // 将jvm进程id保存到pid文件
            File pid = new File("pid");
            if (pid.exists()) {
                pid.delete();
                //LOGGER.error("the pid file is exist at {}", pid.getAbsolutePath());
                //return;
            }
            out = new FileOutputStream(pid);
            out.write(vm.split("@")[0].getBytes());
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }

        LOGGER.info("SyncMain main starting at {}", vm);
        AbstractXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        context.start();
        synchronized (forWait) {
            forWait.wait();
        }
        context.close();
    }

}
