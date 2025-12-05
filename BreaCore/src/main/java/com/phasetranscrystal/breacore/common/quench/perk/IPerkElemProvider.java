package com.phasetranscrystal.breacore.common.quench.perk;

import java.util.Map;

public interface IPerkElemProvider {

    default int extraPerkWeight() {
        return 0;
    }

    default boolean isAddPerkStrength() {
        return false;
    }

    Map<Perk, Double> getPerkAndStrength();
}
