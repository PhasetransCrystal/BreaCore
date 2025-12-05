package com.phasetranscrystal.breacore.common.quench.stuct;

import com.phasetranscrystal.breacore.api.material.Material;
import com.phasetranscrystal.breacore.api.registry.BreaRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.*;
import java.util.function.BiFunction;

public abstract class EquipType {

    public abstract Map<ResourceLocation, EquipAssemblySlot> getSlots();

    public EquipmentSlot availableSlot;

    public ResourceLocation getId() {
        return BreaRegistries.EQUIP_TYPE.getKey(this);
    }

    @Override
    public String toString() {
        return "EquipType(" + getId() + ")";
    }

    /**
     * 锻造系统已经收集了各部件的数据。这个方法用于构造锻造物品的属性修饰表，也可以在这里对你的物品执行额外的操作。
     *
     * @param stack    物品堆实例，其{@link com.phasetranscrystal.breacore.common.quench.EquipAssemblyComponent
     *                 组装部件}的基装备类型为此实例。
     * @param gathered 通过各个部件收集的各数据键的数值。
     */
    public abstract List<ItemAttributeModifiers.Entry> insertData(ItemStack stack, Map<ResourceLocation, Double> gathered,
                                                                  Map<ResourceLocation, EquipType.PartAndRemould> data);

    public record EquipAssemblySlot(PartType type, boolean inMust,
                                    BiFunction<ResourceLocation, Double, Double> valueMapper) {}

    public record PartAndRemould(Material material, Optional<PartRemouldType> remould) {

        public static final Codec<PartAndRemould> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                BreaRegistries.MATERIALS.byNameCodec().fieldOf("material").forGetter(PartAndRemould::material),
                BreaRegistries.PART_REMOULD_TYPE.byNameCodec().optionalFieldOf("remould").forGetter(PartAndRemould::remould)).apply(ins, PartAndRemould::new));
    }
}
