package com.phasetranscrystal.brealib;

import net.neoforged.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(BreaLib.MOD_ID)
public class BreaLib {

    public static final String MOD_ID = "brealib";
    public static final String Core_ID = "breacore";
    public static final String Name = "瓦解核心";
    public static final Logger LOGGER = getLogger(MOD_ID);

    public static Logger getLogger(String module) {
        return getLogger(module, null);
    }

    public static Logger getLogger(String module, @Nullable String content) {
        return LogManager.getLogger("Breakdown." + module + (content == null ? "" : ":" + content));
    }
}
