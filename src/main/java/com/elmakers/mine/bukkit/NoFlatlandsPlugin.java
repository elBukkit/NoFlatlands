package com.elmakers.mine.bukkit;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NoFlatlandsPlugin extends JavaPlugin {
    private ChunkManager chunkManager;

    public void onEnable()
    {
        if (chunkManager == null) {
            chunkManager = new ChunkManager(this);
        }
        saveDefaultConfig();
        Configuration config = getConfig();
        chunkManager.setWorlds(config.getStringList("worlds"));
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(chunkManager, this);
    }

    public void onDisable()
    {
    }
}
