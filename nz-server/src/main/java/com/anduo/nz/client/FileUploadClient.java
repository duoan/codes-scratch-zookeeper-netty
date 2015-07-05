// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.client;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Summary: TODO 描述信息
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/1
 * time   : 23:57
 */
public class FileUploadClient {
    public static final int CLIENT_COUNT = 100;

    public static void main(String args[]) {

        for (int i = 0; i < CLIENT_COUNT; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        uploadFile();
                    } catch (Exception ex) {
                        Logger.getLogger(FileUploadClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();

        }

    }

    private static void uploadFile()
            throws Exception {
        File file = new File("small.jpg");

    }
}
