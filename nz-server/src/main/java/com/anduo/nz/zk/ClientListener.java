// Copyright (C) 2015 anduo
// All rights reserved
package com.anduo.nz.zk;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

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
 * time   : 01:08
 */
public class ClientListener implements Watcher {
    protected static Logger      logger      = LogManager.getLogger(ClientListener.class);
    protected        String      prefixPath  = "";
    protected        ClientNodes clientNodes = new ClientNodes();
    protected        ZKConnect   ZKConnect   = null;
    protected        Integer     LOCK        = Integer.valueOf(0);

    public ClientListener(String prefixPath, ZKConnect ZKConnect) {
        this.prefixPath = prefixPath;
        this.ZKConnect = ZKConnect;
    }

    public BasicDBList records() {
        return this.clientNodes.records;
    }

    public BasicDBObject hashClient(String jobClass, String hashKey) {
        return this.clientNodes.hashClient(jobClass, hashKey);
    }

    public void removeRecord(String id) {
        this.clientNodes.remove(id);
    }

    public void addRecord(String id) {
        if (!this.ZKConnect.checkAlive())
            return;
        try {
            String c = this.ZKConnect.getData(this.prefixPath + "/" + id);
            if (c == null) {
                return;
            }
            BasicDBObject record = (BasicDBObject) JSON.parse(c);
            this.clientNodes.add(record);
        } catch (Exception e) {
            logger.error("ClientListener-->>ServerNodeListener error ", e);
        }
    }

    public void reload() {
        if (!this.ZKConnect.checkAlive()) {
            return;
        }
        if (!this.ZKConnect.exists(this.prefixPath))
            return;
        try {
            String timeStamp = DateUtil.currentDateTime();
            List items = this.ZKConnect.getChildrens(this.prefixPath);
            for (int i = 0; (items != null) && (i < items.size()); i++) {
                String id = (String) items.get(i);
                String c = this.ZKConnect.getData(this.prefixPath + "/" + id);
                if (c == null) {
                    continue;
                }
                BasicDBObject record = (BasicDBObject) JSON.parse(c);
                record.put("timeStamp", timeStamp);
                this.clientNodes.add(record);
            }
            int index = 0;
            while (index < this.clientNodes.records.size()) {
                BasicDBObject record = (BasicDBObject) this.clientNodes.records.get(index);
                if (record.getString("timeStamp").equalsIgnoreCase(timeStamp)) {
                    index++;
                } else if (!this.clientNodes.remove(record.getString("id"))) {
                    index++;
                }
            }
        } catch (Exception e) {
            logger.error("ServerListener-->>reload() error.", e);
        }
    }

    public void process(WatchedEvent event) {
        if (event == null) {
            return;
        }
        String path = event.getPath();
        if ((path == null) || (!path.startsWith(this.prefixPath))) {
            return;
        }
        synchronized (this.LOCK) {
            try {
                String[] values = StringUtil.split(path, '/');
                String id = values.length >= 2 ? values[1] : "";
                BasicDBObject oServer = null;
                if (event.getType() == Event.EventType.NodeCreated) {
                    addRecord(id);
                } else if (event.getType() == Event.EventType.NodeDeleted) {
                    removeRecord(id);
                } else if (event.getType() == Event.EventType.NodeDataChanged) {
                    addRecord(id);
                } else if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    reload();
                }
            } catch (Exception e) {
                logger.error("ServerListener-->>process(" + com.alibaba.fastjson.JSON.toJSONString(event) + ") error.", e);
            }
        }
    }
}
