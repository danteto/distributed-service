package io.dan.distributedservice.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZooKeeperProperties {
    public String url;
    public int session;
}
