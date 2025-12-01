package com.phasetranscrystal.breacore.data.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import com.phasetranscrystal.breacore.api.registry.registrate.BreaRegistrate;
import com.phasetranscrystal.brealib.utils.BreaUtil;
import com.tterrag.registrate.util.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

import static com.phasetranscrystal.breacore.common.registry.BreaRegistration.REGISTRATE;

public class BreaCreativeModeTabs {

    public static RegistryEntry<CreativeModeTab, CreativeModeTab> DEBUG_ITEMS;

    static {
        DEBUG_ITEMS = REGISTRATE.defaultCreativeTab("debug_items", builder -> builder.displayItems(new RegistrateDisplayItemsGenerator("debug_items", REGISTRATE))
                .icon(Items.COMMAND_BLOCK::getDefaultInstance)
                .title(REGISTRATE.addLang("itemsGroup", BreaUtil.byPath("debug_items"), "Debug Page"))
                .build())
                .register();
    }

    public static void init() {}

    public static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        public final String name;
        public final BreaRegistrate registrate;

        public RegistrateDisplayItemsGenerator(String name, BreaRegistrate registrate) {
            this.name = name;
            this.registrate = registrate;
        }

        @Override
        public void accept(@NotNull CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
                           @NotNull CreativeModeTab.Output output) {
            var tab = registrate.get(name, Registries.CREATIVE_MODE_TAB);
            for (var entry : registrate.getAll(Registries.BLOCK)) {
                if (!registrate.isInCreativeTab(entry, tab))
                    continue;
                Item item = entry.get().asItem();
                if (item == Items.AIR)
                    continue;
            }
            for (var entry : registrate.getAll(Registries.ITEM)) {
                if (!registrate.isInCreativeTab(entry, tab))
                    continue;
                Item item = entry.get();
                output.accept(item);

            }
        }
    }
}
