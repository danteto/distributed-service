package io.dan.distributedservice.zookeeper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class ZooKeeperInfoController {

    private ZooKeeperClient zooKeeperClient;

    public ZooKeeperInfoController(ZooKeeperClient zooKeeperClient) {
        this.zooKeeperClient = zooKeeperClient;
    }

    @GetMapping
    public String getInfo() {
        return zooKeeperClient.getLeader("/election");
    }
}
