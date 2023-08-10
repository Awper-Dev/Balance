package me.awper.balance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.awper.balance.balancer.LoadBalancer;
import me.awper.balance.injection.BalanceModule;
import me.awper.balance.servers.ServerPool;
import org.slf4j.Logger;

@Plugin(id = "balance", name = "Balance", authors = {"Awper"}, description = "A lobby player load balancer", version = "1.0")
public class Balance {
    /**
     * Proxy server running the plugin
     */
    private final ProxyServer server;
    /**
     * Main plugin logger
     */
    private final Logger logger;

    @Inject
    public Balance(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    /**
     * Handle initializing of the plugin when proxy server is fully up
     *
     * @param event
     */
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        logger.info("----- [Balance] -----");
        logger.info("       Enabled");
        logger.info("---------------------");

        // Create server pool and load balancer
        Injector injector = Guice.createInjector(new BalanceModule(server));
        ServerPool pool = injector.getInstance(ServerPool.class);
        LoadBalancer balancer = injector.getInstance(LoadBalancer.class);

        // Register balancer as a listener
        server.getEventManager().register(this, balancer);
    }
}
