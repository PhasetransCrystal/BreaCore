package com.phasetranscrystal.breacore.data.tag;

import com.phasetranscrystal.breacore.api.addon.AddonFinder;
import com.phasetranscrystal.breacore.api.addon.IBreaAddon;

public class BreaTagPrefixes {

    public static void init() {
        AddonFinder.getAddonList().forEach(IBreaAddon::registerTagPrefixes);
    }
}
