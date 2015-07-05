package com.anduo.filesync.zk;

/**
 *
*/
public interface NodeListener {
    void nodeChanged(ZKClient sender, ChangedEvent event) throws Exception;
}
