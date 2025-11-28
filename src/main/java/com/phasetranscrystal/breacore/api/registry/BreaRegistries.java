package com.phasetranscrystal.breacore.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

import com.mojang.serialization.MapCodec;
import com.phasetranscrystal.breacore.common.horiz.SavableEventConsumerData;
import com.phasetranscrystal.brealib.utils.BreaUtil;

public class BreaRegistries {

    public static <T> ResourceKey<Registry<T>> makeRegistryKey(ResourceLocation registryId) {
        return ResourceKey.createRegistryKey(registryId);
    }

    public static final ResourceKey<Registry<MapCodec<? extends SavableEventConsumerData<?>>>> SAVABLE_EVENT_CONSUMER_TYPE_KEY = makeRegistryKey(BreaUtil.byPath("horiz/savable_event_consumer"));

    public static final Registry<MapCodec<? extends SavableEventConsumerData<?>>> SAVABLE_EVENT_CONSUMER_TYPE = new RegistryBuilder<>(SAVABLE_EVENT_CONSUMER_TYPE_KEY).create();
}
