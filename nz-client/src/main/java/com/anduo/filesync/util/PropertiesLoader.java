// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.filesync.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

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
 * Summary: TODO 描述信息
 * Author : anduo@qq.com
 * Version: 1.0
 * Date   : 15/7/4
 * time   : 00:50
 */
public class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    public static Properties load(String fileName) {
        return load(fileName, null);
    }

    public static Properties load(String fileName, Properties parent) {

        InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) { return null; }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            Properties prop = new Properties(parent);
            prop.load(reader);
            return prop;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) { is.close(); }
            } catch (IOException e) {
                LOGGER.warn("资源关闭时出错");
            }
        }

    }
}
