package dev.hely.tab.api.versions;

import dev.hely.tab.api.versions.impl.*;
import dev.hely.tab.api.versions.module.IPlayerVersion;
import dev.hely.tab.api.versions.module.PlayerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public class PlayerVersionManager {


    public static IPlayerVersion version;

    public PlayerVersionManager() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        if (serverVersion.equalsIgnoreCase("v1_7_R4")) {
            version = new PlayerVersion1_7Impl();
            return;
        }

        if (pluginManager.getPlugin("ViaVersion") != null) {
            version = new PlayerVersionViaVersionImpl();
            return;
        }

        if (pluginManager.getPlugin("ProtocolSupport") != null) {
            version = new PlayerVersionProtocolSupportImpl();
            return;
        }


        if (pluginManager.getPlugin("ProtocolLib") != null) {
            version = new PlayerVersionProtocolLibImpl();
        }
    }
    public static PlayerVersion getPlayerVersion(Player player) {
        return version.getPlayerVersion(player);
    }

}
