package com.solt.cinematicafk.camera;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateHook {
    /**
     * Safely checks if a player is connected via Floodgate.
     * This class should only be loaded if Floodgate plugin is enabled.
     */
    public static boolean isBedrock(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }
}
