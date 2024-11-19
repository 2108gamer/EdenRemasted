package dev.hely.tab.api.versions.impl;

import dev.hely.tab.api.versions.module.IPlayerVersion;
import dev.hely.tab.api.versions.module.PlayerVersion;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public class PlayerVersionProtocolSupportImpl implements IPlayerVersion
{
    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(ProtocolSupportAPI.getProtocolVersion(player).getId());
    }
}
