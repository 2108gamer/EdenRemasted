package rip.diamond.practice.profile.divisions;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Data @Getter @Setter
public class Division {

        private final String name;
        private final String displayName;
        private final String miniLogo;
        private final Material icon;
        private final int durability;
        private final int winsMin;
        private final int winsMax;

}
