package com.phasetranscrystal.breacore.common.quench;

import com.phasetranscrystal.breacore.common.quench.stuct.EquipType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BreaQuenchTest {
    public static void bootstrapConsumer(@NotNull IEventBus bus) {
    }

    public static class TestSwordEquipType extends EquipType {
//        public static final

        @Override
        public Map<ResourceLocation, EquipAssemblySlot<?>> getSlots() {
            return Map.of();
        }
    }
}
