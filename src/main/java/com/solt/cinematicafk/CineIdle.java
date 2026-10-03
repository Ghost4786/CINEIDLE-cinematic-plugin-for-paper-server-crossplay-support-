package com.solt.cinematicafk;

import com.solt.cinematicafk.afk.AFKManager;
import com.solt.cinematicafk.camera.CameraManager;
import com.solt.cinematicafk.commands.ForceAFKCommand;
import com.solt.cinematicafk.config.ConfigManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class CineIdle extends JavaPlugin {
    
    private ConfigManager configManager;
    private AFKManager afkManager;
    private CameraManager cameraManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.cameraManager = new CameraManager(this);
        this.afkManager = new AFKManager(this);
        
        getServer().getPluginManager().registerEvents(afkManager, this);
        

        // Modern 26.3 Paper Lifecycle Event for Command Registration (Replaces Bukkit CommandMap)
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("forceafk", "Forces a player into AFK cinematic mode", java.util.List.of(), new ForceAFKCommand(this));
            event.registrar().register("testpacket", "Tests GAME_STATE_CHANGE packet structure", java.util.List.of(), new com.solt.cinematicafk.commands.TestPacketCommand());
        });
        
        getLogger().info("CineIdle (Paper 26.3+ Optimized) has been enabled.");
        getLogger().info("Developed by GHOST4786");
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> {
            if (afkManager.isAFK(player)) {
                cameraManager.stopCinematic(player);
            }
        });
        getLogger().info("CineIdle has been safely disabled.");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public AFKManager getAfkManager() { return afkManager; }
    public CameraManager getCameraManager() { return cameraManager; }
}
