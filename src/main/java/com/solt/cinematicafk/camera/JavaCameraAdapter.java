package com.solt.cinematicafk.camera;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.solt.cinematicafk.CineIdle;

public class JavaCameraAdapter {

    private static final Map<UUID, org.bukkit.GameMode> originalGameModes = new HashMap<>();
    // Store generated UUIDs for the clones so we can remove them later
    private static final Map<UUID, UUID> cloneUuids = new HashMap<>();
    public static final Map<UUID, List<Location>> locationHistory = new java.util.concurrent.ConcurrentHashMap<>();

    public static void startCamera(Plugin plugin, CinematicSession session, Location loc) {
        Player player = session.getPlayer();
        int entityId = session.getEntityId();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        try {
            originalGameModes.put(player.getUniqueId(), player.getGameMode());
            player.setAllowFlight(true);
            player.setFlying(true);
            // 1. Set the player to real Spectator mode
            // We do this server-side to ensure the client engine natively processes it without rejecting fake packets.
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);

            // 2. Spawn Fake Player NPC Clone at the original location
            int cloneId = entityId;
            UUID cloneUuid = UUID.randomUUID();
            cloneUuids.put(player.getUniqueId(), cloneUuid);
            
            WrappedGameProfile originalProfile = WrappedGameProfile.fromPlayer(player);
            WrappedGameProfile cloneProfile = new WrappedGameProfile(cloneUuid, player.getName());
            cloneProfile.getProperties().putAll(originalProfile.getProperties());
            
            // Player Info Update (ADD_PLAYER)
            PacketContainer info = pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
            info.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            // In 1.19.3+, PlayerInfoData signature has 7 arguments
            PlayerInfoData infoData = new PlayerInfoData(cloneUuid, 0, false, EnumWrappers.NativeGameMode.SURVIVAL, cloneProfile, null, (com.comphenix.protocol.wrappers.WrappedRemoteChatSessionData) null);
            info.getPlayerInfoDataLists().write(0, Collections.singletonList(infoData));
            pm.sendServerPacket(player, info);
            
            // Spawn Entity (Player)
            PacketContainer spawnNpc = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnNpc.getIntegers().write(0, cloneId);
            spawnNpc.getUUIDs().write(0, cloneUuid);
            spawnNpc.getEntityTypeModifier().write(0, EntityType.PLAYER);
            spawnNpc.getDoubles().write(0, player.getLocation().getX()).write(1, player.getLocation().getY()).write(2, player.getLocation().getZ());
            // In ProtocolLib, byte 0 is Pitch and byte 1 is Yaw. They were inverted, causing the head bug!
            spawnNpc.getBytes().write(0, (byte) (player.getLocation().getPitch() * 256.0F / 360.0F));
            spawnNpc.getBytes().write(1, (byte) (player.getLocation().getYaw() * 256.0F / 360.0F));
            pm.sendServerPacket(player, spawnNpc);
            
            // Explicitly rotate the NPC's head mesh to match the body yaw
            PacketContainer headRotation = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            headRotation.getIntegers().write(0, cloneId);
            headRotation.getBytes().write(0, (byte) (player.getLocation().getYaw() * 256.0F / 360.0F));
            pm.sendServerPacket(player, headRotation);
            
            // Hide the Java NPC Nametag (Fake Scoreboard Team)
            // Using Bukkit API instead of packets to avoid NMS / ProtocolLib OptionalStructure hell
            org.bukkit.scoreboard.Team team = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard().getTeam("AFK_HIDE");
            if (team == null) {
                team = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("AFK_HIDE");
                team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
            }
            team.addEntry(player.getName());
            
            // NPC Metadata (Skin layers) removed to avoid 1.21.x IllegalStateException
            
            // 3. Immediately teleport to guarantee exact Pitch/Yaw alignment
            teleportCamera(plugin, session, loc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void teleportCamera(Plugin plugin, CinematicSession session, Location loc) {
        Player player = session.getPlayer();
        try {
            // Zero Momentum (The Gravity Fix)
            player.setVelocity(new org.bukkit.util.Vector(0, 0, 0));

            // Paper 1.21 removed position-relative flags from the API, causing TeleportFlag.Relative
            // to be treated as VELOCITY flags only. Passing deltaLoc (dx, dy, dz) caused the server
            // to teleport the player to those exact small coordinates (e.g., Y=0.1 -> underground).
            // We must pass the absolute location.
            player.teleport(loc, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
            session.setVirtualCamera(loc);
            
            List<Location> history = locationHistory.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            history.add(loc.clone());
            if (history.size() > 40) history.remove(0);
        } catch (Exception e) {
            System.err.println("CineIdle Teleport Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void stopCamera(Player player, CinematicSession session) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        try {
            locationHistory.remove(player.getUniqueId());
            // 1. Destroy the fake NPC clone
            PacketContainer destroy = pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            List<Integer> entitiesToDestroy = new ArrayList<>();
            entitiesToDestroy.add(session.getEntityId()); // The clone
            destroy.getIntLists().write(0, entitiesToDestroy);
            pm.sendServerPacket(player, destroy);
            
            // 2. Teleport player physically back to where they started AFK
            player.teleport(session.getOriginLoc(), org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
            
            // Remove Clone from Tablist and Team
            UUID cloneUuid = cloneUuids.remove(player.getUniqueId());
            if (cloneUuid != null) {
                PacketContainer info = pm.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
                info.getUUIDLists().write(0, Collections.singletonList(cloneUuid));
                pm.sendServerPacket(player, info);
            }
            
            org.bukkit.scoreboard.Team team = org.bukkit.Bukkit.getScoreboardManager().getMainScoreboard().getTeam("AFK_HIDE");
            if (team != null) {
                team.removeEntry(player.getName());
            }

            // 3. Restore gamemode natively
            org.bukkit.GameMode original = originalGameModes.remove(player.getUniqueId());
            if (original != null) {
                player.setGameMode(original);
            } else {
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
