package com.elmakers.mine.bukkit;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class WorldGuardManager {
	private WorldGuardPlugin worldGuard = null;

	public void initialize(Plugin plugin) {
        try {
            Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
            if (wgPlugin instanceof WorldGuardPlugin) {
                worldGuard = (WorldGuardPlugin)wgPlugin;
            }
        } catch (Throwable ex) {
        }
				
        if (worldGuard == null) {
            plugin.getLogger().info("WorldGuard not found, regions will be ignored.");
        }  else {
            plugin.getLogger().info("WorldGuard found, will ignore chunks in any region");
		}
	}

    public boolean isPassthrough(Location location) {
		if (worldGuard == null || location == null) return true;
				 
		RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        if (regionManager == null) return true;

		ApplicableRegionSet checkSet = regionManager.getApplicableRegions(location);
		if (checkSet == null) return true;
		return checkSet.size() == 0 || checkSet.allows(DefaultFlag.PASSTHROUGH);
	}
}
