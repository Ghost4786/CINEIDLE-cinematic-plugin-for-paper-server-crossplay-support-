package com.solt.cinematicafk.camera;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.solt.cinematicafk.CineIdle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class JavaCameraAdapter {

    public static final Map<UUID, List<Location>> locationHistory = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> cloneUuids = new ConcurrentHashMap<>();
    private static final Map<UUID, ArmorStand> cameraEntities = new ConcurrentHashMap<>();
    
    // Cached Game Event Type for GameMode change (Reason 3)
    private static Object changeGameModeType = null;

    private static Object getChangeGameModeType() {
        if (changeGameModeType != null) return changeGameModeType;
        try {
            Class<?> typeClass = Class.forName("net.minecraft.network.protocol.game.ClientboundGameEventPacket$Type");
            Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundGameEventPacket");
            for (Field field : packetClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == typeClass) {
                    field.setAccessible(true);
                    Object typeObj = field.get(null);
                    for (Field innerField : typeClass.getDeclaredFields()) {
                        if (innerField.getType() == int.class) {
                            innerField.setAccessible(true);
                            int id = innerField.getInt(typeObj);
                            if (id == 3) {
                                changeGameModeType = typeObj;
                                return changeGameModeType;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendGameStateChange(Player player, float gamemodeValue) {
        try {
            Object typeObj = getChangeGameModeType();
            if (typeObj == null) return;
            
            ProtocolManager pm = ProtocolLibrary.getProtocolManager();
            PacketContainer packet = pm.createPacket(PacketType.Play.Server.GAME_STATE_CHANGE);
            packet.getModifier().write(0, typeObj); // Mod 0: Type
            packet.getFloat().write(0, gamemodeValue); // Mod 1 / Float 0: value
            pm.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startCamera(Plugin plugin, CinematicSession session, Location loc) {
        Player player = session.getPlayer();
        int cameraId = session.getEntityId();
        int cloneId = cameraId + 100000; // Generate a unique ID for the clone
        UUID cloneUuid = UUID.randomUUID();
        cloneUuids.put(player.getUniqueId(), cloneUuid);
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        
        try {
            // 1. Send Client-side GameMode change to Spectator
            // This hides the UI/crosshair cleanly without altering server gamemode
            sendGameStateChange(player, 3.0f);

            // 2. Spawn Real Camera Entity (Armor Stand - invisible)
            ArmorStand cameraEntity = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            cameraEntity.setVisible(false);
            cameraEntity.setMarker(false); // MUST BE FALSE! If true, client ignores teleports when spectating!
            cameraEntity.setGravity(false);
            cameraEntity.setInvulnerable(true);
            cameraEntity.setCustomNameVisible(false);
            cameraEntities.put(player.getUniqueId(), cameraEntity);
            
            // Hide it from everyone except the spectating player
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.hideEntity(plugin, cameraEntity);
                }
            }

            int realCameraId = cameraEntity.getEntityId();

            // 3. Instruct Client to spectate the camera entity
            PacketContainer camera = pm.createPacket(PacketType.Play.Server.CAMERA);
            camera.getIntegers().write(0, realCameraId);
            pm.sendServerPacket(player, camera);

            // 4. Spawn Fake NPC Clone of the player at their actual location so they can see their own body
            WrappedGameProfile originalProfile = WrappedGameProfile.fromPlayer(player);
            WrappedGameProfile cloneProfile = new WrappedGameProfile(cloneUuid, player.getName());
            cloneProfile.getProperties().putAll(originalProfile.getProperties());
            
            // Add Player Info for Clone
            PacketContainer info = pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
            info.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            PlayerInfoData infoData = new PlayerInfoData(
                cloneUuid, 0, false, EnumWrappers.NativeGameMode.SURVIVAL, cloneProfile, null, 
                (com.comphenix.protocol.wrappers.WrappedRemoteChatSessionData) null
            );
            info.getPlayerInfoDataLists().write(0, Collections.singletonList(infoData));
            pm.sendServerPacket(player, info);
            
            // Spawn the Clone Entity
            PacketContainer spawnNpc = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            spawnNpc.getIntegers().write(0, cloneId);
            spawnNpc.getUUIDs().write(0, cloneUuid);
            spawnNpc.getEntityTypeModifier().write(0, EntityType.PLAYER);
            spawnNpc.getDoubles().write(0, player.getLocation().getX()).write(1, player.getLocation().getY()).write(2, player.getLocation().getZ());
            spawnNpc.getBytes().write(0, (byte) (player.getLocation().getPitch() * 256.0F / 360.0F));
            spawnNpc.getBytes().write(1, (byte) (player.getLocation().getYaw() * 256.0F / 360.0F));
            pm.sendServerPacket(player, spawnNpc);
            
            PacketContainer headRotation = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            headRotation.getIntegers().write(0, cloneId);
            headRotation.getBytes().write(0, (byte) (player.getLocation().getYaw() * 256.0F / 360.0F));
            pm.sendServerPacket(player, headRotation);

            session.setVirtualCamera(loc);
            List<Location> history = locationHistory.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            history.add(loc.clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void teleportCamera(Plugin plugin, CinematicSession session, Location loc) {
        Player player = session.getPlayer();
        ArmorStand camera = cameraEntities.get(player.getUniqueId());
        
        try {
            if (camera != null && camera.isValid()) {
                camera.teleport(loc);
                
                // Enforce Camera continuously so modified clients cannot dismount and use Freecam
                ProtocolManager pm = ProtocolLibrary.getProtocolManager();
                PacketContainer camPacket = pm.createPacket(PacketType.Play.Server.CAMERA);
                camPacket.getIntegers().write(0, camera.getEntityId());
                pm.sendServerPacket(player, camPacket);
            }

            session.setVirtualCamera(loc);
            
            List<Location> history = locationHistory.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
            history.add(loc.clone());
            if (history.size() > 40) history.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopCamera(Player player, CinematicSession session) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        
        // ALWAYS clean up the real ArmorStand first, regardless of player online status
        ArmorStand cameraEntity = cameraEntities.remove(player.getUniqueId());
        if (cameraEntity != null && cameraEntity.isValid()) {
            cameraEntity.remove();
        }
        
        locationHistory.remove(player.getUniqueId());
        UUID cloneUuid = cloneUuids.remove(player.getUniqueId());
        int cloneId = session.getEntityId() + 100000;
        
        if (!player.isOnline()) return; // Don't send packets to offline players

        try {
            // 1. Revert Camera back to Player
            PacketContainer camera = pm.createPacket(PacketType.Play.Server.CAMERA);
            camera.getIntegers().write(0, player.getEntityId());
            pm.sendServerPacket(player, camera);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            // 2. Destroy the Fake Clone Entity
            PacketContainer destroy = pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            List<Integer> entitiesToDestroy = new ArrayList<>();
            entitiesToDestroy.add(cloneId);
            destroy.getIntLists().write(0, entitiesToDestroy);
            pm.sendServerPacket(player, destroy);
        } catch (Exception e) { e.printStackTrace(); }

        try {
            // Remove Clone from Tablist
            if (cloneUuid != null) {
                PacketContainer info = pm.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
                info.getUUIDLists().write(0, Collections.singletonList(cloneUuid));
                pm.sendServerPacket(player, info);
            }
        } catch (Exception e) { e.printStackTrace(); }

        try {
            // Revert GAME_STATE_CHANGE to restore crosshair and hand
            float nativeModeFloat;
            switch (player.getGameMode()) {
                case CREATIVE: 
                    nativeModeFloat = 1.0f;
                    break;
                case ADVENTURE: 
                    nativeModeFloat = 2.0f;
                    break;
                case SPECTATOR: 
                    nativeModeFloat = 3.0f;
                    break;
                default: 
                    nativeModeFloat = 0.0f;
                    break;
            }
            sendGameStateChange(player, nativeModeFloat);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
