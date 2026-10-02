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
        
        // Hide Spectator mode and Entity destruction from other players to prevent ghosts
        com.comphenix.protocol.ProtocolLibrary.getProtocolManager().addPacketListener(
            new com.comphenix.protocol.events.PacketAdapter(this, 
                com.comphenix.protocol.events.ListenerPriority.NORMAL, 
                com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO,
                com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY) {
                @Override
                public void onPacketSending(com.comphenix.protocol.events.PacketEvent event) {
                    if (event.getPacketType() == com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY) {
                        java.util.List<Integer> ids = new java.util.ArrayList<>(event.getPacket().getIntLists().read(0));
                        boolean changed = false;
                        for (org.bukkit.entity.Player p : getServer().getOnlinePlayers()) {
                            if (afkManager.isAFK(p) && p != event.getPlayer() && ids.contains(p.getEntityId())) {
                                ids.remove((Integer) p.getEntityId());
                                changed = true;
                            }
                        }
                        if (changed) {
                            event.getPacket().getIntLists().write(0, ids);
                        }
                    } else if (event.getPacketType() == com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO) {
                        try {
                            java.util.List<com.comphenix.protocol.wrappers.PlayerInfoData> dataList = event.getPacket().getPlayerInfoDataLists().read(0);
                            java.util.List<com.comphenix.protocol.wrappers.PlayerInfoData> newDataList = new java.util.ArrayList<>();
                            boolean changed = false;
                            
                            for (com.comphenix.protocol.wrappers.PlayerInfoData data : dataList) {
                                org.bukkit.entity.Player p = getServer().getPlayer(data.getProfileId());
                                if (p != null && afkManager.isAFK(p) && p != event.getPlayer() && data.getGameMode() == com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode.SPECTATOR) {
                                    // Rewrite gamemode to survival to prevent grey name in tab
                                    com.comphenix.protocol.wrappers.PlayerInfoData newData = new com.comphenix.protocol.wrappers.PlayerInfoData(
                                        data.getProfileId(),
                                        data.getLatency(),
                                        data.isListed(),
                                        com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode.SURVIVAL,
                                        data.getProfile(),
                                        data.getDisplayName()
                                    );
                                    newDataList.add(newData);
                                    changed = true;
                                } else {
                                    newDataList.add(data);
                                }
                            }
                            
                            if (changed) {
                                event.getPacket().getPlayerInfoDataLists().write(0, newDataList);
                            }
                        } catch (Exception e) {
                            // Silently ignore malformed PLAYER_INFO packets
                        }
                    }
                }
            }
        );
        
        // Modern 26.3 Paper Lifecycle Event for Command Registration (Replaces Bukkit CommandMap)
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register("forceafk", "Forces a player into AFK cinematic mode", java.util.List.of(), new ForceAFKCommand(this));
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
