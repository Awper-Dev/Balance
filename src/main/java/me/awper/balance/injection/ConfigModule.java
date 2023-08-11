package me.awper.balance.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import me.awper.balance.config.ConfigDirectory;
import me.awper.balance.config.ConfigFile;

import java.io.File;
import java.nio.file.Path;

public class ConfigModule extends AbstractModule {

    private Path dataDirectory;

    public ConfigModule(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Provides
    @ConfigDirectory
    public File provideConfigDirectory() {
        return dataDirectory.toFile();
    }

    @Provides
    @ConfigFile
    public File provideConfigFile() {
        return new File(dataDirectory.toString(), "config.json");
    }

    @Provides
    public Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        return builder.create();
    }
}
