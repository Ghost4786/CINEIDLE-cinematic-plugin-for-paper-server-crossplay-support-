package com.solt.cinematicafk.camera;

import com.solt.cinematicafk.CineIdle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CameraManager {
    private final CineIdle plugin;
    private final Map<UUID, CinematicSession> sessions = new ConcurrentHashMap<>();
    private final AtomicInteger entityIdCounter = new AtomicInteger(999999);

    public CameraManager(CineIdle plugin) {
        this.plugin = plugin;
        
        // CRITICAL: Strictly synchronous thread for safe world.rayTraceBlocks execution.
        // Paper's AsyncCatcher will throw an IllegalStateException if rayTraceBlocks is run async.
        new BukkitRunnable() {
            @Override
            public void run() {
                tickSessions();
            }
        }.runTaskTimer(plugin, 0L, 1L); 
    }

    public void startCinematic(Player player) {
        if (sessions.containsKey(player.getUniqueId())) return;

        boolean isBedrock = Bukkit.getPluginManager().isPluginEnabled("floodgate") && FloodgateHook.isBedrock(player);
        int entityId = entityIdCounter.incrementAndGet();
        
        CinematicSession session = new CinematicSession(plugin, player, isBedrock, entityId);
        sessions.put(player.getUniqueId(), session);
        
        // Bedrock Physics Anchor Fix (Prevents falling through geometry)
        Location anchor = player.getLocation();
        anchor.setY(anchor.getBlockY() + 0.1);
        player.teleport(anchor);
        player.setAllowFlight(true);
        player.setFlying(true);

        double distMult = plugin.getConfigManager().getCameraDistanceMultiplier();
        Location startLoc = session.getCurrentShot().getCameraLocation(session.getOriginLoc(), session.getOriginEyeLoc(), 0.0, distMult);
        
        if (!isBedrock) {
            JavaCameraAdapter.startCamera(plugin, session, startLoc);
        } else {
            BedrockCameraAdapter.updateBedrockCamera(player, startLoc, 0.0); 
        }
    }

    public void stopCinematic(Player player) {
        CinematicSession session = sessions.remove(player.getUniqueId());
        if (session != null) {
            player.setAllowFlight(session.getOriginalAllowFlight());
            player.setFlying(session.getOriginalFlying());
            
            if (!session.isBedrock()) {
                JavaCameraAdapter.stopCamera(player, session);
            } else {
                BedrockCameraAdapter.clearBedrockCamera(player);
            }
        }
    }

    private void tickSessions() {
        for (CinematicSession session : sessions.values()) {
            Player player = session.getPlayer();
            if (!player.isOnline()) {
                sessions.remove(player.getUniqueId());
                continue;
            }

            int tick = session.getTickCount();
            int duration = session.getDurationTicks();
            
            if (tick >= duration) {
                session.pickNextShot();
                tick = 0;
            }

            double progress = (tick % duration) / (double) duration;
            double distMult = plugin.getConfigManager().getCameraDistanceMultiplier();
            
            try {
                // Hide any Action Bar "Locator" plugins by constantly overwriting it with an empty space
                if (tick % 5 == 0) {
                    player.sendActionBar(net.kyori.adventure.text.Component.text(" "));
                }

                if (session.isBedrock()) {
                    // Update Bedrock camera every 10 ticks (0.5 seconds)
                    if (tick % 10 == 0) {
                        double targetProgress = Math.min((tick + 10) / (double) duration, 1.0);
                        Location targetLoc = session.getCurrentShot().getCameraLocation(session.getOriginLoc(), session.getOriginEyeLoc(), targetProgress, distMult);
                        BedrockCameraAdapter.updateBedrockCamera(player, targetLoc, 0.5);
                    }
                } else {
                    Location currentLoc = session.getCurrentShot().getCameraLocation(session.getOriginLoc(), session.getOriginEyeLoc(), progress, distMult);
                    JavaCameraAdapter.teleportCamera(plugin, session, currentLoc);
                }
            } catch (Exception e) {
                System.err.println("CineIdle tick error for " + player.getName() + ": " + e.getMessage());
            }

            session.incrementTick();
        }
    }
}
