package com.anduo.filesync.zk;

/**
 *
 */
public interface ConnectionStateListener {
    void stateChanged(ZKClient sender, ConnectionState state);
}
