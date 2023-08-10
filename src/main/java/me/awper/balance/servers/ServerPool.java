package me.awper.balance.servers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Singleton
public class ServerPool {
    /**
     * All servers registered at the proxy
     */
    private List<RegisteredServer> registeredServers;

    @Inject
    public ServerPool(List<RegisteredServer> registeredServers) {
        this.registeredServers = registeredServers;
    }

    /**
     * Provides the most optimal server in terms of player count
     *
     * @return RegisteredServer, the server to connect the next client to
     */
    public RegisteredServer provideNext() {
        // Determine the server with the smallest amount of connected players
        Optional<RegisteredServer> optionalRegisteredServer = registeredServers.stream()
                .min(Comparator.comparingInt(registeredServer -> registeredServer.getPlayersConnected().size()));

        // Only return when everything went well
        if (optionalRegisteredServer.isPresent()) {
            return optionalRegisteredServer.get();
        }

        // Throw an error when no optimal server can be found
        throw new NoNextServerException();
    }
}
