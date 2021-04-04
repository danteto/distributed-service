package io.dan.distributedservice.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

public interface ZooKeeperClient {

    void connect(Watcher watcher);

    String createNode(String node, boolean watch, NodeType nodeType);

    boolean setWatch(final String node, final boolean watch);

    List<String> getChildren(final String node, final boolean watch);

    String getLeader(String path);
}
