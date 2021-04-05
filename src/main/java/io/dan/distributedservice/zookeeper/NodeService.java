package io.dan.distributedservice.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NodeService {
    private static final Logger log = LoggerFactory.getLogger(NodeService.class);

    private ZooKeeperClient zooKeeperClient;
    private static final String ELECTION_PATH = "/election";
    private static final String NODE = "/node_";
    private String nodePath;
    private String watchedNodePath;


    public NodeService(ZooKeeperClient zooKeeperClient) {
        this.zooKeeperClient = zooKeeperClient;
    }

    public void execute() {
        zooKeeperClient.connect(new NodeWatcher());
        NodeInfo.setServiceName(System.getenv("SERVICE_NAME"));
        log.info("### Node with name: " + NodeInfo.getServiceName() + " has started");

        final String electionPath = zooKeeperClient.createNode(ELECTION_PATH, NodeType.PERSISTENT);
        if (electionPath == null) {
            throw new IllegalStateException("Unable to create leader election path");
        }

        nodePath = zooKeeperClient.createNode(electionPath.concat(NODE), NodeType.EPHEMERAL_SEQUENTIAL);
        if (nodePath == null) {
            throw new IllegalStateException("Unable to create node in leader election path: ");
        }
        NodeInfo.setServiceName(System.getenv("SERVICE_NAME"));
        NodeInfo.setPath(nodePath);

        log.error("[Node: " + NodeInfo.getServiceName() + "] Process node created with path: " + nodePath);

        attemptForLeader();
    }


    public void attemptForLeader() {
        final List<String> children = zooKeeperClient.getChildren(ELECTION_PATH);
        Collections.sort(children);

        if (!children.isEmpty()) {
            String leaderName = children.get(0);
            NodeInfo.setLeaderNodePath(leaderName);
        }
        int index = children.indexOf(nodePath.substring(nodePath.lastIndexOf('/') + 1));
        if (index == 0) {
            log.info("[Node: " + NodeInfo.getServiceName() + "] I am the new leader!");
        } else {
            final String watchedNodeShortPath = children.get(index - 1);
            watchedNodePath = ELECTION_PATH.concat("/").concat(watchedNodeShortPath);

            log.info("[Node: " + NodeInfo.getServiceName() + "] - Setting watch on node with path: " + watchedNodePath);

            zooKeeperClient.setWatch(watchedNodePath, true);
        }
    }


    @Component
    public class NodeWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            log.info("[Node: " + NodeInfo.getServiceName() + "] Event received: " + event.getType());

            final Event.EventType eventType = event.getType();
            if (eventType.equals(Event.EventType.NodeDeleted)) {
                if (event.getPath().equalsIgnoreCase(watchedNodePath)) {
                    attemptForLeader();
                }
            }
        }


    }
}
