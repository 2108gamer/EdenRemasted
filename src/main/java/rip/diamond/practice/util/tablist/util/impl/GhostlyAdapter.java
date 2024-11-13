package rip.diamond.practice.util.tablist.util.impl;

import org.bukkit.entity.Player;
import rip.diamond.practice.util.tablist.util.BufferedTabObject;

import java.util.Set;

public interface GhostlyAdapter {

    Set<BufferedTabObject> getSlots(Player player);

    String getFooter();
    String getHeader();

}