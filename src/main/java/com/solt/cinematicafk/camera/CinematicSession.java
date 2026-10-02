package com.solt.cinematicafk.camera;

import com.solt.cinematicafk.CineIdle;
import com.solt.cinematicafk.camera.angles.CinematicAngles;
import com.solt.cinematicafk.camera.angles.CinematicScenes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class CinematicSession {
    private final Player player;
    private final Location originLoc;
    private final Location originEyeLoc;
    private final boolean isBedrock;
    private final int entityId;
    
    private CinematicAngles currentShot;
    private int tickCount = 0;
    private int currentShotIndex = -1;
    private final int durationTicks;
    private Location virtualCamera;
    
    private final java.util.List<CinematicAngles> playlist = new java.util.ArrayList<>();

    private final boolean originalAllowFlight;
    private final boolean originalFlying;

    public CinematicSession(CineIdle plugin, Player player, boolean isBedrock, int entityId) {
        this.player = player;
        this.originLoc = player.getLocation();
        this.originEyeLoc = this.originLoc.clone().add(0, 1.62, 0);
        this.isBedrock = isBedrock;
        this.entityId = entityId;
        this.durationTicks = plugin.getConfigManager().getTransitionSpeedTicks();
        this.originalAllowFlight = player.getAllowFlight();
        this.originalFlying = player.isFlying();
        this.virtualCamera = this.originLoc.clone();
        
        pickNextShot();
    }

    public void pickNextShot() {
        currentShotIndex++;
        if (currentShotIndex >= playlist.size()) {
            // Pick a brand new choreographed scene!
            CinematicScenes[] allScenes = CinematicScenes.values();
            CinematicScenes selectedScene = allScenes[new Random().nextInt(allScenes.length)];
            
            playlist.clear();
            playlist.addAll(java.util.Arrays.asList(selectedScene.getSequence()));
            currentShotIndex = 0;
            
            player.sendMessage("§8[§cCinematic§8] §7Now playing scene: §f" + selectedScene.name().replace("SCENE_", "").replace("_", " "));
        }
        this.currentShot = playlist.get(currentShotIndex);
        this.tickCount = 0;
    }

    public Player getPlayer() { return player; }
    public Location getOriginLoc() { return originLoc; }
    public Location getOriginEyeLoc() { return originEyeLoc; }
    public boolean isBedrock() { return isBedrock; }
    public int getEntityId() { return entityId; }
    
    public CinematicAngles getCurrentShot() { return currentShot; }
    public int getTickCount() { return tickCount; }
    public int getDurationTicks() { return durationTicks; }
    public boolean getOriginalAllowFlight() { return originalAllowFlight; }
    public boolean getOriginalFlying() { return originalFlying; }
    
    public Location getVirtualCamera() { return virtualCamera; }
    public void setVirtualCamera(Location loc) { this.virtualCamera = loc; }
    
    public void incrementTick() {
        tickCount++;
    }
}
