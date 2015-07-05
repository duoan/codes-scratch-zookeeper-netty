// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.msg;

import java.io.Serializable;

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
 * Summary: 文件传输对象
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 17:20
 */
public class FileMsg implements Serializable {
    private static final long serialVersionUID = 8953150675564212795L;
    /**
     * 总包数.
     */
    private long   sumCountPackage;
    /**
     * 当前包数.
     */
    private long   countPackage;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件来源数据节点
     */
    private String srcNode;
    /**
     * 文件md5值，防止文件在网路传输过程中对包
     */
    private String fileMd5;//
    /**
     * 文件内容字节数组
     */
    private byte[] bytes;//

    /**
     * @return the sumCountPackage
     */
    public long getSumCountPackage() {
        return sumCountPackage;
    }

    /**
     * @param sumCountPackage the sumCountPackage to set
     */
    public void setSumCountPackage(long sumCountPackage) {
        this.sumCountPackage = sumCountPackage;
    }

    /**
     * @return the countPackage
     */
    public long getCountPackage() {
        return countPackage;
    }

    public void setCountPackage(long countPackage) {
        this.countPackage = countPackage;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(String srcNode) {
        this.srcNode = srcNode;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

}
