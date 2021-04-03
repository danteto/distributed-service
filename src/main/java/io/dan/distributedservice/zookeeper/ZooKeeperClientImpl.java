package io.dan.distributedservice.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ZooKeeperClientImpl implements ZooKeeperClient {
    private final static Logger log = LoggerFactory.getLogger(ZooKeeperClientImpl.class);

    private ZooKeeper zooKeeper;
    private ZooKeeperProperties zooKeeperProperties;

    public ZooKeeperClientImpl(ZooKeeperProperties zooKeeperProperties) {
        this.zooKeeperProperties = zooKeeperProperties;
    }

    @Override
    public void connect(Watcher watcher) {
        try {
            zooKeeper = new ZooKeeper("zookeeper:2181", 5_000, watcher);
        } catch (IOException e) {
            log.debug("### ZOOKEEPER HAS NOT BEEN CONNECTED");
            e.printStackTrace();
        }
    }

    @Override
    public String createNode(String path, boolean isWatched, NodeType nodeType) {
        String createdPath;
        try {
            final Stat stat = zooKeeper.exists(path, isWatched);

            if (stat == null) {
                createdPath = zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, toNodeType(nodeType));
            } else {
                createdPath = path;
            }
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        return createdPath;
    }

    @Override
    public boolean setWatch(String node, boolean isWatched) {
        try {
            final Stat nodeStat = zooKeeper.exists(node, isWatched);

            return nodeStat != null;
        } catch (KeeperException | InterruptedException e) {
            e.getLocalizedMessage();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<String> getChildren(String node, boolean isWatched) {
        try {
            return zooKeeper.getChildren(node, isWatched);
        } catch (KeeperException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private CreateMode toNodeType(NodeType nodeType) {
        switch (nodeType) {
            case EPHEMERAL:
                return CreateMode.EPHEMERAL;
            case EPHEMERAL_SEQUENTIAL:
                return CreateMode.EPHEMERAL_SEQUENTIAL;
        }

        return CreateMode.PERSISTENT;
    }
}
