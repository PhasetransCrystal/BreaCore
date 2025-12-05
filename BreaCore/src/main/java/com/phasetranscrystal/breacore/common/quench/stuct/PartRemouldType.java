package com.phasetranscrystal.breacore.common.quench.stuct;

import com.phasetranscrystal.breacore.api.registry.BreaRegistries;
import com.phasetranscrystal.breacore.common.quench.IValueElemProvider;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

public abstract class PartRemouldType implements IValueElemProvider {

    public abstract PartType getRootPart();

    public Set<ResourceLocation> requiredProcessingMac() {
        return Set.of();
    }

    public ResourceLocation getId() {
        return BreaRegistries.PART_REMOULD_TYPE.getKey(this);
    }

    @Override
    public String toString() {
        return "PartRemouldType(" + getId() + ")";
    }

    @Override
    public Map<ResourceLocation, Double> getValues() {
        return Map.of();
    }
}
