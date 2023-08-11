package me.awper.balance.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import me.awper.balance.config.ConfigManager;
import me.awper.balance.config.PoolConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BalanceModule extends AbstractModule {
    /**
     * The proxy server running the balancer
     */
    private ProxyServer proxyServer;

    public BalanceModule(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    /**
     * Provides all servers registered at the proxy that can be pinged
     *
     * @return List<RegisteredServer>, the list of pingable registered servers
     */
    @Provides
    List<RegisteredServer> provideServers(ConfigManager configManager) {
        PoolConfiguration configuration = configManager.getConfiguration();

        return proxyServer.getAllServers().stream().filter(registeredServer -> {
            ServerInfo info = registeredServer.getServerInfo();
            String serverName = info.getName();

            if (!configuration.serverIsConfigured(serverName)) {
                return false;
            }

            try {
                return registeredServer.ping().thenApply(Objects::nonNull).get();
            } catch (InterruptedException | ExecutionException e) {
                return false;
            }
        }).toList();
    }
}
