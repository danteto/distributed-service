package io.dan.distributedservice.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZooKeeperProperties {
    private String host;
    private String port;
    private int session;

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public int getSession() {
        return session;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setSession(int session) {
        this.session = session;
    }
}
