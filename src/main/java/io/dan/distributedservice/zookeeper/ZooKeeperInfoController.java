package io.dan.distributedservice.zookeeper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/info")
public class ZooKeeperInfoController {
    private ZooKeeperClient zooKeeperClient;

    public ZooKeeperInfoController(ZooKeeperClient zooKeeperClient) {
        this.zooKeeperClient = zooKeeperClient;
    }

    @GetMapping
    public String getInfo() {
        List<String> activeNodes = zooKeeperClient.getActiveNodes();

        return String.format("Current service: %s \n Current node path: %s \n, Leader node path: %s \n All active nodes: %s",
                NodeInfo.getServiceName(),
                NodeInfo.getPath(),
                NodeInfo.getLeaderNodePath(),
                activeNodes);
    }
}
