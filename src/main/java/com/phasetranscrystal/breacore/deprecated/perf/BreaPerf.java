package com.phasetranscrystal.breacore.deprecated.perf;

import com.phasetranscrystal.breacore.common.horiz.EventDistributorTest;
import com.phasetranscrystal.brealib.utils.BreaUtil;
import net.neoforged.bus.api.IEventBus;

public class BreaPerf {
    public static final String MODULE_ID = "perf";
    public static final String MODULE_NAME = "Perf";

    public static void bootstrap(IEventBus bus) {
        if (BreaUtil.isProd()) {
            EventDistributorTest.bootstrapConsumer(bus);
        }
    }

//    public static final RegistryEntry<DataComponentType<?>, DataComponentType<ItemStackPerformanceGroup>> ITEM_STACK_PERFPACK;
//
//    static {
//        ITEM_STACK_PERFPACK = Brea.simple("perf/item_stack_perfpack",
//                Registries.DATA_COMPONENT_TYPE,
//                () -> DataComponentType.<ItemStackPerformanceGroup>builder().persistent(ItemStackPerformanceGroup.CODEC_UNIT).build());
//    }

}
