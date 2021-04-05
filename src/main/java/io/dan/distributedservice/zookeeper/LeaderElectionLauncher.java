package io.dan.distributedservice.zookeeper;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class LeaderElectionLauncher implements ApplicationListener<ContextRefreshedEvent> {
    private NodeService nodeService;

    public LeaderElectionLauncher(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        nodeService.execute();
    }
}
