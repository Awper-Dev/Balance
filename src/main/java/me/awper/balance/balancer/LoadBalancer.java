package me.awper.balance.balancer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.awper.balance.servers.ServerPool;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class LoadBalancer {
    /**
     * LoadBalancer logger
     */
    private Logger logger;

    /**
     * Pool of servers connected to the proxy
     */
    private ServerPool pool;

    @Inject
    public LoadBalancer(ServerPool pool, Logger logger) {
        this.pool = pool;
        this.logger = logger;
    }

    /**
     * Listens for incoming player connections
     * Redirects players to server chosen by load balancer
     *
     * @param event PostLoginEvent, the player connection event
     */
    @Subscribe
    public void onPlayerServerChoice(PostLoginEvent event) {
        Player player = event.getPlayer();
        // Initial server chosen by proxy and configuration
        Optional<ServerConnection> attemptedServerConnectionOptional = player.getCurrentServer();
        // Determine optimal server
        RegisteredServer balancedServer = pool.provideNext();

        // If no initial server can be found, connect to optimal server
        if (attemptedServerConnectionOptional.isEmpty()) {
            attemptBalancedConnection(player, balancedServer);
            return;
        }

        ServerConnection attemptedServerConnection = attemptedServerConnectionOptional.get();
        RegisteredServer attemptedServer = attemptedServerConnection.getServer();

        // Only connect to optimal server if not already doing so
        if (!attemptedServer.equals(balancedServer)) {
            attemptBalancedConnection(player, balancedServer);
        }
    }

    /**
     * Attempts to connect the player to the server chosen by the load balancer
     *
     * @param player           Player, the connecting player
     * @param registeredServer RegisteredServer, the server chosen by the load balancer
     */
    private void attemptBalancedConnection(Player player, RegisteredServer registeredServer) {
        // Build a connection to the server
        ConnectionRequestBuilder requestBuilder = player.createConnectionRequest(registeredServer);

        // Attempt connection
        requestBuilder.connect()
                // Handle connection result async
                .thenAcceptAsync(result -> {
                    // Log connection problems
                    if (!result.isSuccessful()) {
                        logger.log(Level.WARNING, createConnectionFailedMessage(player, registeredServer));
                    }
                });
    }

    /**
     * Generates an error message based on the connecting player and chosen server
     *
     * @param player Player, connecting player
     * @param server RegisteredServer, the server that was attempted for connection
     * @return String, the error message
     */
    private String createConnectionFailedMessage(Player player, RegisteredServer server) {
        return "Tried connecting " + player.getUsername() + " to " + server.getServerInfo().getName();
    }
}
