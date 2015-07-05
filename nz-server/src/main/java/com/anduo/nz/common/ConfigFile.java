// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.common;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ResourceBundle;

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
 * Date   : 15/7/2
 * time   : 00:57
 */
public class ConfigFile {

    private static final Logger LOGGER = LogManager.getLogger(ConfigFile.class);

    private        ResourceBundle rb           = null;
    private static ConfigFile     commonConfig = null;
    private static ConfigFile     zkConfig     = null;

    static {
        LOGGER.info("load commonConfig&zkConfig properities.");
        commonConfig = new ConfigFile(ResourceBundle.getBundle("commonConfig"));
        zkConfig = new ConfigFile(ResourceBundle.getBundle("zkConfig"));
    }

    public ConfigFile(ResourceBundle rb) {
        this.rb = rb;
    }

    public static ConfigFile commonConfig() {
        return commonConfig;
    }

    public static ConfigFile zkConfig() {return zkConfig;}

    public String getItem(String item, String defaultValue) {
        String value = null;
        if (this.rb != null) {
            try {
                value = this.rb.getString(item.trim());
                value = value.trim();
            } catch (Exception e) {
                value = defaultValue;
            }
        }
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public int getIntItem(String item, String defaultValue) {
        int i = 0;
        String value = getItem(item, defaultValue);
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.info(e.getMessage());
        }
        return i;
    }

    public long getLongItem(String item, String defaultValue) {
        long i = 0;
        String value = getItem(item, defaultValue);
        try {
            i = Long.valueOf(value);
        } catch (NumberFormatException e) {
            LOGGER.info(e.getMessage());
        }
        return i;
    }

    public double getDoubleItem(String item, String defaultValue) {
        double i = 0;
        String value = getItem(item, defaultValue);
        try {
            i = Double.valueOf(value);
        } catch (NumberFormatException e) {
            LOGGER.info(e.getMessage());
        }
        return i;
    }

    public boolean getBooleanItem(String item, boolean defaultValue) {
        boolean b = false;
        String value = getItem(item, new Boolean(defaultValue).toString());
        if ((value != null) && (value.equalsIgnoreCase("true"))) {
            b = true;
        }
        return b;
    }

    protected String getNodeValue(Node _node) {
        if (_node == null) {
            return null;
        }
        Node _firstChild = _node.getFirstChild();
        if (_firstChild == null) {
            return null;
        }
        String _text = _firstChild.getNodeValue();
        if (_text != null) {
            _text = _text.trim();
        }
        return _text;
    }

}
