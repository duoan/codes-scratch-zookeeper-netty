package com.anduo.filesync.zk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ZKClientCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKClientCache.class);

    private static final Map<String, ZKClientImpl> CACHE = new HashMap<String, ZKClientImpl>();

    public synchronized static ZKClient get(String address) {
        LOGGER.info("Get zkclient for {}", address);
        ZKClientImpl client = CACHE.get(address);
        if (client == null) {
            CACHE.put(address, new ZKClientImpl(address));
        }
        client = CACHE.get(address);
        client.incrementReference();
        return client;
    }
}
