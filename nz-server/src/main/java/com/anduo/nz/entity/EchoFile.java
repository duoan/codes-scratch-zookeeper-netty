// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.entity;

import java.io.Serializable;

/**
 * Summary: EchoFile
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 00:18
 */
public class EchoFile implements Serializable {
    private static final long serialVersionUID = 8953150675564212795L;
    /**
     * 总包数.
     */
    private int    sumCountPackage;
    /**
     * 当前包数.
     */
    private int    countPackage;
    /**
     * 文件名
     */
    private String file_md5;//
    /**
     * 文件内容字节数组
     */
    private byte[] bytes;//

    public EchoFile() {
    }

    public EchoFile(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return the sumCountPackage
     */
    public int getSumCountPackage() {
        return sumCountPackage;
    }

    /**
     * @param sumCountPackage the sumCountPackage to set
     */
    public void setSumCountPackage(int sumCountPackage) {
        this.sumCountPackage = sumCountPackage;
    }

    /**
     * @return the countPackage
     */
    public int getCountPackage() {
        return countPackage;
    }

    public void setCountPackage(int countPackage) {
        this.countPackage = countPackage;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getFile_md5() {
        return file_md5;
    }

    public void setFile_md5(String file_md5) {
        this.file_md5 = file_md5;
    }
}