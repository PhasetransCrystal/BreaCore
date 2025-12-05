package com.phasetranscrystal.breacore;

import com.phasetranscrystal.brealib.BreaLib;

import com.phasetranscrystal.breacore.client.ClientProxy;
import com.phasetranscrystal.breacore.common.CommonProxy;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import com.tterrag.registrate.util.RegistrateDistExecutor;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BreaCore.MOD_ID)
public class BreaCore {

    public static final Logger LOGGER = BreaLib.getLogger(BreaCore.MOD_ID);
    public static final String MOD_ID = BreaLib.Core_ID;
    public static final String NAME = BreaLib.Name;
    @Getter
    private static ModContainer modContainer;
    @Getter
    private static IEventBus modEventBus;

    public BreaCore(ModContainer container, IEventBus modEventBus) {
        BreaCore.modContainer = container;
        BreaCore.modEventBus = modEventBus;
        RegistrateDistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    }
}
