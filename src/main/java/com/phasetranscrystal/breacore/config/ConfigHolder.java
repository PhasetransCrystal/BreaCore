package com.phasetranscrystal.breacore.config;

import com.phasetranscrystal.breacore.BreaCore;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.Configurable.UpdateRestriction;
import dev.toma.configuration.config.UpdateRestrictions;
import dev.toma.configuration.config.format.ConfigFormats;

@Config(id = BreaCore.MOD_ID + "-common", group = "phasetranscrystal")
public class ConfigHolder {

    public static ConfigHolder INSTANCE;
    private static final Object Lock = new Object();

    public static void init() {
        synchronized (Lock) {
            if (INSTANCE == null)
                Configuration.registerConfig(ConfigHolder.class, ConfigFormats.YAML).getConfigInstance();
        }
    }

    @Configurable
    @Configurable.Comment("进入debug模式")
    @UpdateRestriction(UpdateRestrictions.GAME_RESTART)
    boolean debugMode = false;
}
