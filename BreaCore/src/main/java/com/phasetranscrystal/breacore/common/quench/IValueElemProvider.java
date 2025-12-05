package com.phasetranscrystal.breacore.common.quench;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IValueElemProvider {

    Map<ResourceLocation, Double> getValues();

    default void merge(Map<ResourceLocation, Double> values) {
        for (Map.Entry<ResourceLocation, Double> entry : values.entrySet()) {
            values.merge(entry.getKey(), entry.getValue(), Double::sum);
        }
    }
}
