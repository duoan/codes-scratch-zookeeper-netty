package com.anduo.filesync.zk;

/**
 *
 */
public class ChangedEvent {
    public enum Type {
        CHILD_ADDED,
        CHILD_UPDATED,
        CHILD_REMOVED
    }

    private String path;
    private Type   type;

    public ChangedEvent(String path, Type type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return this.path;
    }

    public Type getType() {
        return type;
    }
}
