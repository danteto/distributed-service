package io.dan.distributedservice.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NodeService implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(NodeService.class);

    private ZooKeeperClient zooKeeperClient;
    private static final String ELECTION_NODE = "/election";
    private static final String NODE = "/node_";
    private final String name;
    private String nodePath;
    private String watchedNodePath;


    public NodeService(ZooKeeperClient zooKeeperClient) {
        name = System.getenv("SERVICE_NAME");
        this.zooKeeperClient = zooKeeperClient;
    }

    @Override
    public void run() {
        zooKeeperClient.connect(new NodeWatcher());
        log.info("### Node with name: " + name + " has started");

        final String electionPath = zooKeeperClient.createNode(ELECTION_NODE, false, NodeType.PERSISTENT);
        if (electionPath == null) {
            throw new IllegalStateException("Unable to create leader election path");
        }

        nodePath = zooKeeperClient.createNode(electionPath.concat(NODE), false, NodeType.EPHEMERAL_SEQUENTIAL);
        if (nodePath == null) {
            throw new IllegalStateException("Unable to create node in leader election path: ");
        }

        log.debug("[Node: " + name + "] Process node created with path: " + nodePath);

        attemptForLeader();
    }


    public void attemptForLeader() {
        final List<String> children = zooKeeperClient.getChildren(ELECTION_NODE, false);
        Collections.sort(children);

        int index = children.indexOf(nodePath.substring(nodePath.lastIndexOf('/') + 1));
        if (index == 0) {
            log.info("[Node: " + name + "] I am the new leader!");
        } else {
            final String watchedNodeShortPath = children.get(index - 1);
            watchedNodePath = ELECTION_NODE.concat("/").concat(watchedNodeShortPath);

            log.info("[Node: " + name + "] - Setting watch on node with path: " + watchedNodePath);

            zooKeeperClient.setWatch(watchedNodePath, true);
        }
    }


    @Component
    public class NodeWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            log.info("[Node: " + name + "] Event received: " + event);

            final Event.EventType eventType = event.getType();
            if (eventType.equals(Event.EventType.NodeDeleted)) {
                if (event.getPath().equalsIgnoreCase(watchedNodePath)) {
                    attemptForLeader();
                }
            }
        }


    }
}
