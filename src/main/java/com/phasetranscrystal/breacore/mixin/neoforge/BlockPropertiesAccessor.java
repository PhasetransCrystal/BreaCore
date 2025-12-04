package com.phasetranscrystal.breacore.mixin.neoforge;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@Mixin(BlockBehaviour.Properties.class)
public interface BlockPropertiesAccessor {

    @Accessor(remap = false)
    float getDestroyTime();

    @Accessor(remap = false)
    float getExplosionResistance();

    @Accessor(remap = false)
    boolean isHasCollision();

    @Accessor(remap = false)
    boolean isIsRandomlyTicking();

    @Accessor(remap = false)
    ToIntFunction<BlockState> getLightEmission();

    @Accessor(remap = false)
    Function<BlockState, MapColor> getMapColor();

    @Accessor(remap = false)
    SoundType getSoundType();

    @Accessor(remap = false)
    float getFriction();

    @Accessor(remap = false)
    float getSpeedFactor();

    @Accessor(remap = false)
    boolean isDynamicShape();

    @Accessor(remap = false)
    boolean isCanOcclude();

    @Accessor(remap = false)
    boolean isIsAir();

    @Accessor(remap = false)
    boolean isIgnitedByLava();

    @Accessor(remap = false)
    boolean isLiquid();

    @Accessor(remap = false)
    boolean isForceSolidOff();

    @Accessor(remap = false)
    boolean isForceSolidOn();

    @Accessor(remap = false)
    PushReaction getPushReaction();

    @Accessor(remap = false)
    boolean isRequiresCorrectToolForDrops();

    @Nullable
    @Accessor(remap = false)
    BlockBehaviour.OffsetFunction getOffsetFunction();

    @Accessor(remap = false)
    void setOffsetFunction(@Nullable BlockBehaviour.OffsetFunction function);

    @Accessor(remap = false)
    boolean isSpawnTerrainParticles();

    @Accessor(remap = false)
    FeatureFlagSet getRequiredFeatures();

    @Accessor(remap = false)
    void setRequiredFeatures(FeatureFlagSet set);

    @Accessor(remap = false)
    BlockBehaviour.StatePredicate getEmissiveRendering();

    @Accessor(remap = false)
    NoteBlockInstrument getInstrument();

    @Accessor(remap = false)
    boolean isReplaceable();
}
