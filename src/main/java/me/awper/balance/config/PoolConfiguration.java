package me.awper.balance.config;

import java.util.ArrayList;
import java.util.List;

public class PoolConfiguration {
    /**
     * List of server names to adapt into pool
     */
    private List<String> configuredServers = new ArrayList<>();


    /**
     * Returns the list of whitelisted server names
     *
     * @return List<String>, list of server names
     */
    public List<String> getConfiguredServers() {
        return configuredServers;
    }

    /**
     * Returns true whenever the server is configured to be used by the balancer
     *
     * @param name String, name of the server to check
     * @return Boolean, whether the balancer should make use of the given server
     */
    public boolean serverIsConfigured(String name) {
        return configuredServers.contains(name);
    }

    public void addServer(String name) {
        configuredServers.add(name);
    }

    public void removeServer(String name) {
        configuredServers.remove(name);
    }
}
