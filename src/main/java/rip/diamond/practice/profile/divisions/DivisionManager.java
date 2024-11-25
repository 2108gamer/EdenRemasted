package rip.diamond.practice.profile.divisions;

import lombok.Getter;
import org.bukkit.Material;
import rip.diamond.practice.Eden;
import rip.diamond.practice.util.BasicConfigFile;
import rip.diamond.practice.util.Common;

import java.util.ArrayList;
import java.util.List;

public class DivisionManager {

    @Getter
    private static final List<Division> divisions = new ArrayList<>();

    public static void loadDivisions() {
        BasicConfigFile file = Eden.INSTANCE.getDivisionsFile();
        divisions.clear();
        if (file.getConfiguration().contains("DIVISIONS.RANKS")) {
            Common.debug("Cargando divisiones...");
            for (String path : file.getConfiguration().getConfigurationSection("DIVISIONS.RANKS").getKeys(false)) {
                Common.debug("Cargando división: " + path);
                Division division = loadDivision("DIVISIONS.RANKS." + path);
                if (division != null) {
                    divisions.add(division);
                    Common.debug("División cargada: " + path);
                }
            }
        } else {
           Common.debug("No se encontraron divisiones en la configuración.");
        }
    }

    public static Division loadDivision(String path) {
        BasicConfigFile file = Eden.INSTANCE.getDivisionsFile();
        try {
            String displayName = file.getString(path + ".DISPLAY_NAME");
            String miniLogo = file.getString(path + ".MINI-LOGO");
            Material icon = Material.valueOf(file.getString(path + ".ICON"));
            int durability = file.getInt(path + ".DURABILITY");
            int winsMin = file.getInt(path + ".WINS-MIN");
            int winsMax = file.getInt(path + ".WINS-MAX");
            return new Division(path, displayName, miniLogo, icon, durability, winsMin, winsMax);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Division getDivisionByWins(int wins, List<Division> divisions) {
        for (Division division : divisions) {
            if (wins >= division.getWinsMin() && wins <= division.getWinsMax()) {
                return division;
            }
        }
        return null;
    }

    public static Division getDivisionByName(String name) {
        for (Division division : divisions) {
            if (division.getDisplayName().equalsIgnoreCase(name)) {
                return division;
            }
        }
        return null;
    }

    public static Division getNextDivision(Division currentDivision) {
        for (int i = 0; i < divisions.size(); i++) {
            if (divisions.get(i).equals(currentDivision) && i + 1 < divisions.size()) {
                return divisions.get(i + 1);
            }
        }
        return null;
    }
}
