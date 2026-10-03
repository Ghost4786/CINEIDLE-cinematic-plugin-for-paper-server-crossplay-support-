package com.solt.cinematicafk.afk;

import com.solt.cinematicafk.CineIdle;
import io.papermc.paper.event.player.AsyncChatEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AFKManager implements Listener {
    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();
    private final Map<UUID, Long> afkStartTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Float[]> lastPacketRotations = new ConcurrentHashMap<>();
    private final Set<UUID> afkPlayers = new HashSet<>();
    private final CineIdle plugin;

    public AFKManager(CineIdle plugin) {
        this.plugin = plugin;
        
        // Synchronous poller ensures Player.getLocation() does not trip AsyncCatcher logic in Paper
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long timeoutMillis = plugin.getConfigManager().getAfkTriggerTimeSeconds() * 1000;
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (afkPlayers.contains(player.getUniqueId())) continue;
                    
                    Location lastLoc = lastLocations.get(player.getUniqueId());
                    Location currentLoc = player.getLocation();
                    
                    if (lastLoc == null || 
                        lastLoc.getX() != currentLoc.getX() || 
                        lastLoc.getY() != currentLoc.getY() || 
                        lastLoc.getZ() != currentLoc.getZ() || 
                        lastLoc.getYaw() != currentLoc.getYaw() || 
                        lastLoc.getPitch() != currentLoc.getPitch()) {
                        
                        lastLocations.put(player.getUniqueId(), currentLoc);
                        lastActivity.put(player.getUniqueId(), now);
                    }
                    
                    long last = lastActivity.getOrDefault(player.getUniqueId(), now);
                    if (now - last > timeoutMillis) {
                        setAFK(player, true);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Synchronous task run safely every 1s
        
    }

    public void forceAFK(Player player) {
        setAFK(player, true);
    }

    public void updateActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        lastLocations.put(player.getUniqueId(), player.getLocation());
        if (afkPlayers.contains(player.getUniqueId())) {
            // Check grace period (500ms) to ignore immediate echoes when first teleporting
            Long startTime = afkStartTimes.get(player.getUniqueId());
            if (startTime != null && (System.currentTimeMillis() - startTime) < 500) {
                return;
            }
            // Un-AFK synchronously on the main thread safely
            Bukkit.getScheduler().runTask(plugin, () -> setAFK(player, false));
        }
    }
    
    public void setAFK(Player player, boolean isAfk) {
        if (isAfk) {
            afkPlayers.add(player.getUniqueId());
            afkStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage("§8[§eCineIdle§8] §7You are now AFK. Entering cinematic mode...");
            plugin.getCameraManager().startCinematic(player);
        } else {
            afkPlayers.remove(player.getUniqueId());
            afkStartTimes.remove(player.getUniqueId());
            lastPacketRotations.remove(player.getUniqueId());
            player.sendMessage("§8[§eCineIdle§8] §7You are no longer AFK.");
            plugin.getCameraManager().stopCinematic(player);
        }
    }

    public boolean isAFK(Player player) {
        return afkPlayers.contains(player.getUniqueId());
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!afkPlayers.contains(e.getPlayer().getUniqueId())) return;
        
        Location from = e.getFrom();
        Location to = e.getTo();
        
        // Ignore rotation-only movements (like looking around while AFK)
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }
        
        updateActivity(e.getPlayer());
    }
    
    @EventHandler public void onChat(AsyncChatEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onInteract(PlayerInteractEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onToggleSneak(PlayerToggleSneakEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onAnimation(PlayerAnimationEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onItemHeld(org.bukkit.event.player.PlayerItemHeldEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onSwapHand(org.bukkit.event.player.PlayerSwapHandItemsEvent e) { updateActivity(e.getPlayer()); }
    @EventHandler public void onDropItem(org.bukkit.event.player.PlayerDropItemEvent e) { updateActivity(e.getPlayer()); }
    
    @EventHandler 
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            // Immediate cleanup on damage
            updateActivity(p);
        }
    }
    
    @EventHandler public void onJoin(PlayerJoinEvent e) { updateActivity(e.getPlayer()); }
    
    @EventHandler 
    public void onQuit(PlayerQuitEvent e) { 
        // Critical Cleanup Safeguards avoiding memory leak ghosts
        lastActivity.remove(e.getPlayer().getUniqueId());
        lastLocations.remove(e.getPlayer().getUniqueId());
        afkStartTimes.remove(e.getPlayer().getUniqueId());
        
        if (afkPlayers.remove(e.getPlayer().getUniqueId())) {
            // Automatically invokes destroy packets & clears Bedrock adapters
            plugin.getCameraManager().stopCinematic(e.getPlayer());
        }
    }
}
