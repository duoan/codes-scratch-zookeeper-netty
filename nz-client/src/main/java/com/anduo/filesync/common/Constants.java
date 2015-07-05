// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.common;

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
 * Summary: 常量
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/2
 * time   : 22:32
 */
public class Constants {

    /**
     * 配置文件路径
     */
    private static final String NAME_CONFIG_PATH               = "name.properties";
    private static final String INIT_CONFIG_PATH               = "local-nodes.properties";
    /**
     * NODE节点相关配置路径
     */
    public static final  String CONFIG_NODES                   = "/myapp/config/nodes";
    /**
     * 公用配置信息路径
     */
    public static final  String CONFIG_COMMON                  = "/myapp/config/commoncfg";
    /**
     * 节点注册路径
     */
    public static final  String NODE_ROOT                      = "/myapp/nodes";
    /**
     * zookeeper服务器地址的properties参数名,在properties文件中设置
     */
    public static final  String ZOOKEEPER_SERVERS_PRO          = "zk.servers";
    /**
     * 所有配置所在zookeeper的根节点的 的properties参数名,在properties文件中设置
     */
    public static final  String ZOOKEEPER_CONFIG_ROOT_PATH_PRO = "zk.config.root.path";
    /**
     * 项目配置数据的根节点
     */
    public static final  String CONFIG_ROOT_PATH               = "/myapp/config";

    public static final String DEFAULT_DATA_FILE_PATH = "Data";
    public static final int    RANDOM_FILE_COUNT      = 10;
}
