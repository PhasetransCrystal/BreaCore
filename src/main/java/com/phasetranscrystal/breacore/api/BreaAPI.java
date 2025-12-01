package com.phasetranscrystal.breacore.api;

import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.registries.RegisterEvent;

import com.phasetranscrystal.breacore.api.material.registry.IMaterialRegistry;
import com.phasetranscrystal.breacore.api.registry.BreaRegistry;
import com.phasetranscrystal.brealib.mixin.registrate.neoforge.RegisterEventAccessor;
import org.jetbrains.annotations.ApiStatus;

public class BreaAPI {

    /**
     * <p/>
     * This is worth exactly one normal Item.
     * This Constant can be divided by many commonly used Numbers such as
     * 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, ... 64 or 81
     * without loosing precision and is for that reason used as Unit of Amount.
     * But it is also small enough to be multiplied with larger Numbers.
     * <p/>
     * This is used to determine the amount of Material contained inside a prefixed Ore.
     * For example Nugget = M / 9 as it contains out of 1/9 of an Ingot.
     */
    public static final long M = 3628800;
    /**
     * Renamed from "FLUID_MATERIAL_UNIT" to just "L"
     * <p/>
     * Fluid per Material Unit (Prime Factors: 3 * 3 * 2 * 2 * 2 * 2)
     */
    public static final int L = 144;
    public static IMaterialRegistry materialManager;

    /**
     * Post the register event for a specific (GT) registrate. Internal use only, do not attempt to call this.
     */
    @ApiStatus.Internal
    public static <T> void postRegisterEvent(BreaRegistry<T> registry) {
        RegisterEvent registerEvent = RegisterEventAccessor.create(registry.key(), registry);
        ModLoader.postEventWrapContainerInModOrder(registerEvent);
    }
}
