package com.solt.cinematicafk.camera;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.bedrock.camera.CameraEaseType;
import org.geysermc.geyser.api.bedrock.camera.CameraPosition;
import org.geysermc.geyser.api.connection.GeyserConnection;

public class BedrockCameraAdapter {
    
    public static void updateBedrockCamera(Player player, Location targetLoc, double easeTimeSeconds) {
        try {
            GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUniqueId());
            // Safe null-check before applying Bedrock camera states
            if (connection != null) {
                connection.camera().sendCameraPosition(
                    CameraPosition.builder()
                        .position(Vector3f.from((float) targetLoc.getX(), (float) targetLoc.getY(), (float) targetLoc.getZ()))
                        .facingPosition(Vector3f.from((float) player.getEyeLocation().getX(), (float) player.getEyeLocation().getY(), (float) player.getEyeLocation().getZ()))
                        .easeType(CameraEaseType.LINEAR)
                        .easeSeconds((float) easeTimeSeconds)
                        .build()
                );
            }
        } catch (Exception e) {
            // Geyser potentially unavailable for this call
        }
    }

    public static void clearBedrockCamera(Player player) {
        try {
            GeyserConnection connection = GeyserApi.api().connectionByUuid(player.getUniqueId());
            // Graceful safe exit null-check 
            if (connection != null) {
                connection.camera().clearCameraInstructions();
            }
        } catch (Exception e) {
            // Geyser potentially unavailable during disconnect
        }
    }
}
