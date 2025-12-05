package com.phasetranscrystal.breacore.common.quench;

import com.phasetranscrystal.breacore.api.attribute.IAttributeModifierProvider;
import com.phasetranscrystal.breacore.api.item.TagPrefixItem;
import com.phasetranscrystal.breacore.api.material.ChemicalHelper;
import com.phasetranscrystal.breacore.api.material.Material;
import com.phasetranscrystal.breacore.api.material.property.PropertyKey;
import com.phasetranscrystal.breacore.api.registry.BreaRegistries;
import com.phasetranscrystal.breacore.common.quench.perk.EquipPerkComponent;
import com.phasetranscrystal.breacore.common.quench.perk.IPerkElemProvider;
import com.phasetranscrystal.breacore.common.quench.perk.Perk;
import com.phasetranscrystal.breacore.common.quench.stuct.EquipType;
import com.phasetranscrystal.breacore.common.quench.stuct.PartRemouldType;
import com.phasetranscrystal.breacore.common.quench.stuct.PartType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * 装备组装信息组件。内容只应在装备被重新锻造时发生变化
 *
 * @param type  装备类型 决定了装备的基本装配模型，即这是什么类型的装备。
 * @param slots 装备槽位 即该装备组装的每个槽位上装配的内容是什么，分为该部件的材料与该部件使用的改进工艺(可空)两部分。
 */
public record EquipAssemblyComponent(EquipType type, boolean finishedAssembly,
                                     Map<ResourceLocation, EquipType.PartAndRemould> slots,
                                     List<ItemAttributeModifiers.Entry> entries)
        implements IAttributeModifierProvider, IPerkElemProvider {

    public static final Codec<EquipAssemblyComponent> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BreaRegistries.EQUIP_TYPE.byNameCodec().fieldOf("type").forGetter(EquipAssemblyComponent::type),
            Codec.BOOL.fieldOf("finished").forGetter(EquipAssemblyComponent::finishedAssembly),
            Codec.unboundedMap(ResourceLocation.CODEC, EquipType.PartAndRemould.CODEC).fieldOf("slots").forGetter(EquipAssemblyComponent::slots),
            ItemAttributeModifiers.Entry.CODEC.listOf().fieldOf("entries").forGetter(EquipAssemblyComponent::entries)).apply(ins, EquipAssemblyComponent::new));

    public EquipAssemblyComponent(EquipType type) {
        this(type, false, Map.of(), List.of());
    }

    @Override
    public List<ItemAttributeModifiers.Entry> getEntries() {
        return finishedAssembly ? entries : List.of();
    }

    @Override
    public Map<Perk, Double> getPerkAndStrength() {
        if (!finishedAssembly) return Map.of();

        Map<Perk, Double> map = new HashMap<>();
        type.getSlots().forEach((rl, slot) -> {
            if (slots.containsKey(rl))
                slots.get(rl).material().getProperty(slot.type().finderKey()).getPerksAndStrength().forEach((perk, value) -> {
                    map.merge(perk, value, Double::max);
                });
        });
        return map;
    }

    public static Mutable toMutable(ItemStack stack) {
        EquipAssemblyComponent component = stack.get(BreaQuench.EQUIP_ASSEMBLY_COMPONENT);
        if (component == null) return null;
        return component._toMutable(stack);
    }

    private Mutable _toMutable(ItemStack thiz) {
        return new Mutable(this, thiz);
    }

    public static class Mutable {

        public static final Pair<PartRemouldType, ItemStack> EMPTY_PAIR = Pair.of(null, ItemStack.EMPTY);
        public final EquipType equipType;
        private final ItemStack stack;
        private boolean isFinished;
        private final Map<ResourceLocation, Material> pose2MaterialOrigin = new HashMap<>();
        private final Map<ResourceLocation, PartRemouldType> pose2RemouldOrigin = new HashMap<>();
        private final Map<ResourceLocation, ItemStack> pose2Stack = new HashMap<>();
        private final Map<ResourceLocation, Optional<PartRemouldType>> pose2Remould = new HashMap<>();
        private final Map<ResourceLocation, Integer> requiredProcessingMac = new HashMap<>();

        @Getter
        private boolean changed = true;

        public Mutable(EquipAssemblyComponent component, ItemStack stack) {
            this.equipType = component.type;
            this.stack = stack;
            component.slots.forEach((loc, g) -> {
                if (g != null) {
                    pose2MaterialOrigin.put(loc, g.material());
                    g.remould().ifPresent(remould -> pose2RemouldOrigin.put(loc, remould));
                }
            });
            checkReadyForAssembly();
        }

        // ---[部件处理]---

        /**
         * 检查能否将物品放置在某槽位上
         */
        public boolean canPlaceAt(ResourceLocation pos, ItemStack stack) {
            return equipType.getSlots().containsKey(pos) && matchType(stack, equipType.getSlots().get(pos).type());
        }

        /**
         * 使用一个物品堆与当前物品槽位进行交换
         *
         * @param stack 新的物品堆
         */
        public ItemStack interactWith(ResourceLocation pos, ItemStack stack) {
            ItemStack exist = pose2Stack.getOrDefault(pos, ItemStack.EMPTY);
            if ((stack.isEmpty() && exist.isEmpty()) || ItemStack.isSameItemSameComponents(stack, exist))
                return stack;
            if (stack.isEmpty() || canPlaceAt(pos, stack)) {// 用空堆交换或放置非空堆
                pose2Stack.put(pos, stack);
                checkReadyForAssembly();
                return exist;
            }
            return stack;
        }

        /**
         * 检查某一槽位上是否有物品，不论是新放置的还是继承原有数据
         */
        public Material getMaterialOnPose(ResourceLocation pos) {
            if (!equipType.getSlots().containsKey(pos)) return null;
            var mstack = ChemicalHelper.getMaterialStack(pose2Stack.getOrDefault(pos, ItemStack.EMPTY));
            return mstack.isEmpty() ? pose2MaterialOrigin.get(pos) : mstack.material();
        }

        // ---[改装处理]---

        /**
         * 检查是否能在对应槽位设定改造类型
         */
        public boolean canSetRemouldTypeAt(ResourceLocation pos, PartRemouldType remouldType) {
            return equipType.getSlots().containsKey(pos) && // 保证该类型装备有此槽位
                    (remouldType == null || equipType.getSlots().get(pos).type().equals(remouldType.getRootPart()));// 改造类型为空
            // 或
            // 根类型与部件类型一致
        }

        /**
         * 设置槽位改装类型。会进行先行检查。
         * 改装消耗的资源会在之后统一计算，不会影响检查结果。
         *
         * @param remouldType 改装类型。可为null，表示不使用任何改装。
         */
        public boolean setRemouldType(ResourceLocation pos, @Nullable PartRemouldType remouldType) {
            if (!canSetRemouldTypeAt(pos, remouldType))
                return false;
            if (remouldType == pose2RemouldOrigin.get(pos)) {// 若设置的类型与原类型一致，移除改造变动
                pose2Remould.remove(pos);
            } else {// 否则添加改动
                pose2Remould.put(pos, Optional.ofNullable(remouldType));
            }
            checkReadyForAssembly();
            return true;
        }

        /**
         * 检查改装是否发生了变动。
         */
        public boolean hasRemouldChanged(ResourceLocation pos) {
            return equipType.getSlots().containsKey(pos) && pose2Remould.containsKey(pos);
        }

        /**
         * 移除一个位置的改装
         */
        public boolean removeRemouldChange(ResourceLocation pos) {
            if (!hasRemouldChanged(pos)) return false;
            pose2Remould.remove(pos);
            checkReadyForAssembly();
            return true;
        }

        public PartRemouldType getRemouldOnPose(ResourceLocation pos) {
            if (!equipType.getSlots().containsKey(pos)) return null;
            var remould = pose2Remould.get(pos);
            if (remould != null) return remould.orElse(null);
            return pose2RemouldOrigin.get(pos);
        }

        // ---[变动检查]---

        private void checkReadyForAssembly() {
            for (ResourceLocation rl : equipType.getSlots().keySet()) {
                boolean inMust = equipType.getSlots().get(rl).inMust(); // 是否为必须
                Material material = getMaterialOnPose(rl);

                if (material == null && inMust) {// 如果位置必要且空白
                    this.isFinished = false;
                }

                if (material != null) {
                    pose2Remould.getOrDefault(rl, Optional.ofNullable(pose2RemouldOrigin.get(rl))).ifPresent(remould -> {
                        remould.requiredProcessingMac().forEach(mac -> {
                            requiredProcessingMac.merge(mac, material.getProperty(PropertyKey.DUST).getHarvestLevel(), Math::max);
                        });
                    });
                }
            }
            // TODO 检查加工机器

            this.isFinished = true;
        }

        public boolean isReadyForAssembly() {
            return isFinished;
        }

        public Map<ResourceLocation, Integer> getProcessingMacRequired() {
            return ImmutableMap.copyOf(requiredProcessingMac);
        }

        // ---[数据回收]---

        /**
         * 弹出当前缓存的所有物品
         */
        public List<ItemStack> stackOut() {
            List<ItemStack> list = new ArrayList<>(this.pose2Stack.values());
            this.pose2Stack.clear();
            checkReadyForAssembly();
            return list;
        }

        public List<ItemStack> clearChange() {
            this.pose2Remould.clear();
            return stackOut();
        }

        public void consumeChanged() {
            this.changed = false;
        }

        /**
         * 将完成锻造的装备的信息存入物品。
         *
         * @see EquipAssemblyComponent 锻造装备数据存储组件
         */
        public boolean updateValueToStack() {
            // 先行检查是否符合锻造条件
            checkReadyForAssembly();
            if (!isFinished) return false;

            Map<ResourceLocation, Double> values = new HashMap<>();
            Map<ResourceLocation, EquipType.PartAndRemould> slots = new HashMap<>();

            for (ResourceLocation rl : equipType.getSlots().keySet()) {
                PartRemouldType remould = getRemouldOnPose(rl);
                Material material = getMaterialOnPose(rl);
                if (material != null)
                    slots.put(rl, new EquipType.PartAndRemould(material, Optional.ofNullable(remould)));
            }

            slots = ImmutableMap.copyOf(slots);

            if (stack.getItem() instanceof IValueElemProvider provider) {
                values.putAll(provider.getValues());
            }

            slots.forEach((rl, data) -> {
                EquipType.EquipAssemblySlot slot = equipType.getSlots().get(rl);
                data.material().getProperty(slot.type().finderKey()).getValues(slot.type()).getValues().forEach((key, value) -> {
                    values.merge(key, slot.valueMapper().apply(key, value), Double::sum);
                });
                data.remould().ifPresent(remould -> remould.merge(values));
            });

            List<ItemAttributeModifiers.Entry> list = equipType.insertData(stack, values, slots);
            EquipAssemblyComponent component = new EquipAssemblyComponent(equipType, true, slots, list);

            stack.set(BreaQuench.EQUIP_ASSEMBLY_COMPONENT, component);
            EquipPerkComponent.update(stack);
            return true;
        }

        private static boolean matchType(ItemStack stack, PartType type) {
            return stack.getItem() instanceof TagPrefixItem tpi &&
                    tpi.tagPrefix == type.requiredType() &&
                    tpi.material.hasProperty(type.finderKey()) &&
                    tpi.material.getProperty(type.finderKey()).getValues(type) != null;
        }
    }
}
