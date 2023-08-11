package me.awper.balance.config;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Singleton
public class ConfigManager {
    /** Directory containing the configuration file */
    private File configDirectory;
    /** Persistent file containing the configuration */
    private File configFile;
    /** Gson instance */
    private Gson gson;
    /** Current configuration */
    private PoolConfiguration configuration;

    @Inject
    public ConfigManager(@ConfigDirectory File configDirectory, @ConfigFile File configFile, Gson gson) throws IOException {
        this.configDirectory = configDirectory;
        this.configFile = configFile;
        this.gson = gson;

        setup();
    }

    /**
     * Saves the current, in-memory, configuration to the config file
     * @throws IOException
     */
    public void save() throws IOException {
        String serialized = gson.toJson(configuration);
        Files.writeString(configFile.toPath(), serialized);
    }

    /**
     * Creates the config directory and files if they do not exist
     * @throws IOException
     */
    public void ensureExistence() throws IOException {
        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        if (!configFile.exists()) {
            configFile.createNewFile();
            writeDefault();
        }
    }

    /**
     * Writes a default configuration after generating the config file
     * @throws IOException
     */
    private void writeDefault() throws IOException {
        configuration = new PoolConfiguration();
        configuration.addServer("server_1");
        configuration.addServer("server_2");

        save();
    }

    /**
     * Creates the config file where needed and loads in the configuration
     * @throws IOException
     */
    public void setup() throws IOException {
        ensureExistence();
        loadConfiguration();
    }

    /**
     * Loads the configuration from the config file
     * @throws IOException
     */
    private void loadConfiguration() throws IOException {
        String contents = Files.readString(configFile.toPath());
        configuration = gson.fromJson(contents, PoolConfiguration.class);
    }

    /**
     * Returns the current configuration
     * @return PoolConfiguration, the current configuration
     */
    public PoolConfiguration getConfiguration() {
        return configuration;
    }
}
