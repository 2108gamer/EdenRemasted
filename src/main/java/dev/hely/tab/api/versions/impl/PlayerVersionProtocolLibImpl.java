package dev.hely.tab.api.versions.impl;

import com.comphenix.protocol.ProtocolLibrary;
import dev.hely.tab.api.versions.module.IPlayerVersion;
import dev.hely.tab.api.versions.module.PlayerVersion;
import org.bukkit.entity.Player;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */

public class PlayerVersionProtocolLibImpl implements IPlayerVersion
{
    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(ProtocolLibrary.getProtocolManager().getProtocolVersion(player));
    }
}
