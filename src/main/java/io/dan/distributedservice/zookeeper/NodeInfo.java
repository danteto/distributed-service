package io.dan.distributedservice.zookeeper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = "singleton")
public class NodeInfo {
    private static String serviceName;
    private static String path;
    private static String leaderNodePath;

    public static String getServiceName() {
        return serviceName;
    }

    public static void setServiceName(String serviceName) {
        NodeInfo.serviceName = serviceName;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        NodeInfo.path = path;
    }

    public static String getLeaderNodePath() {
        return leaderNodePath;
    }

    public static void setLeaderNodePath(String leaderName) {
        NodeInfo.leaderNodePath = leaderName;
    }
}
