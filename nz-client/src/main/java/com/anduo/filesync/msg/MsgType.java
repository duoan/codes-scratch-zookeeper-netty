// Copyright (C) 2015 meituan
// All rights reserved
package com.anduo.filesync.msg;

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
 * Summary: msg的类型,目的是为了在传输过程中对文件进行相应的业务处理
 * Author : anduo@meituan.com
 * Version: 1.0
 * Date   : 15/7/5
 * time   : 22:34
 */
public class MsgType {
    public static final byte HEADER  = 0x01;
    public static final byte BODY    = 0x02;
    public static final byte TAILER  = 0x03;
    public static final byte COMMAND = 0x04;

}
