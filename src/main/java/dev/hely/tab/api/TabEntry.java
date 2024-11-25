package dev.hely.tab.api;

import dev.hely.tab.api.skin.Skin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */

@Getter
@Setter
@AllArgsConstructor
public class TabEntry {

    private String id;
    private OfflinePlayer offlinePlayer;
    private String text;
    private PlayerTablist tab;
    private Skin skin;
    private TabColumn column;
    private int slot, rawSlot, latency;

}
