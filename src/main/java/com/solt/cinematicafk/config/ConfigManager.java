package com.solt.cinematicafk.config;

import com.solt.cinematicafk.CineIdle;

public class ConfigManager {
    private final CineIdle plugin;
    private long afkTriggerTimeSeconds;
    private double cameraDistanceMultiplier;
    private int transitionSpeedTicks;

    public ConfigManager(CineIdle plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        this.afkTriggerTimeSeconds = plugin.getConfig().getLong("afk-trigger-time-seconds", 300);
        this.cameraDistanceMultiplier = plugin.getConfig().getDouble("camera-distance-multiplier", 1.0);
        this.transitionSpeedTicks = plugin.getConfig().getInt("transition-speed-ticks", 1600);
    }

    public long getAfkTriggerTimeSeconds() { return afkTriggerTimeSeconds; }
    public double getCameraDistanceMultiplier() { return cameraDistanceMultiplier; }
    public int getTransitionSpeedTicks() { return transitionSpeedTicks; }
}
