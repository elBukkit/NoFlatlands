package com.elmakers.mine.bukkit;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ChunkManager implements Listener {
    private int chunkListLength = 2048;
    private int chunkRegenDelayMin = 10;
    private int chunkRegenDelayMax = 20;
    private int chunkMaxSearchHeight = 70;
    private int chunkMinSearchHeight = 5;
    private int chunkMinX = 8;
    private int chunkMaxX = 8;
    private int chunkMinZ = 8;
    private int chunkMaxZ = 8;
    private final Random random = new Random();
    private final WorldGuardManager manager;

    private final Plugin plugin;
    private final Set<String> worlds = new HashSet<String>();
    private final TreeSet<String> checked = new TreeSet<String>();

    public ChunkManager(Plugin plugin) {
        this.plugin = plugin;
        this.manager = new WorldGuardManager();
        manager.initialize(plugin);
    }

    public void setWorlds(Collection<String> worldNames) {
        worlds.clear();
        if (worldNames != null) {
            worlds.addAll(worldNames);
        }

        if (worlds.size() > 0) {
            plugin.getLogger().info("Regenerating flatland chunks in: " + StringUtils.join(worlds, ", "));
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!worlds.contains(e.getWorld().getName())) return;

        final Chunk chunk = e.getChunk();
        String chunkKey = chunk.getX() + "," + chunk.getZ();
        if (checked.contains(chunkKey)) return;

        checked.add(chunkKey);
        if (checked.size() > chunkListLength) {
            String oldestKey = checked.pollFirst();
            if (oldestKey != null) {
                checked.remove(oldestKey);
            }
        }

        final World world = chunk.getWorld();
        Location location = chunk.getBlock(8, 64, 8).getLocation();
        if (!manager.isPassthrough(location)) return;

        for (int y = chunkMinSearchHeight; y <= chunkMaxSearchHeight; y++) {
            for (int x = chunkMinX; x <= chunkMaxX; x++) {
                for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                    if (chunk.getBlock(x, y, z).getType() != Material.AIR) return;
                }
            }
        }

        int chunkRegenDelay = chunkRegenDelayMin + random.nextInt(chunkRegenDelayMin + chunkRegenDelayMax);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                world.regenerateChunk(chunk.getX(), chunk.getZ());
            }
        }, chunkRegenDelay);
    }
}
