package io.dan.distributedservice;

import io.dan.distributedservice.zookeeper.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionLauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger log = LoggerFactory.getLogger(LeaderElectionLauncher.class);
    private NodeService nodeService;

    public LeaderElectionLauncher(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        nodeService.run();
    }
}
