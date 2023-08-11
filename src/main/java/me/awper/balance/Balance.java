package me.awper.balance;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.awper.balance.balancer.LoadBalancer;
import me.awper.balance.config.ConfigManager;
import me.awper.balance.injection.BalanceModule;
import me.awper.balance.injection.ConfigModule;
import me.awper.balance.servers.ServerPool;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

    /**
     * Path to config folder
     */
    private final Path dataDirectory;

    @Inject
    public Balance(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    /**
     * Handle initializing of the plugin when proxy server is fully up
     *
     * @param event
     */
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) throws IOException {
        logger.info("----- [Balance] -----");
        logger.info("       Enabled");
        logger.info("---------------------");

        Injector injector = Guice.createInjector(new BalanceModule(server), new ConfigModule(dataDirectory));

        // Create configuration manager
        ConfigManager configManager = injector.getInstance(ConfigManager.class);

        // Create server pool and load balancer
        ServerPool pool = injector.getInstance(ServerPool.class);
        LoadBalancer balancer = injector.getInstance(LoadBalancer.class);

        // Register balancer as a listener
        server.getEventManager().register(this, balancer);
    }
}
