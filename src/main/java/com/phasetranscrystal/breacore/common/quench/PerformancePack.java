package com.phasetranscrystal.breacore.common.quench;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.phasetranscrystal.breacore.common.horiz.BreaHoriz;
import com.phasetranscrystal.breacore.common.horiz.EventDistributor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.Event;

import java.util.*;
import java.util.function.Consumer;

public class PerformancePack {
    public final ResourceLocation[] eventPath;
    public final ResourceLocation combinePath;
    public final Map<Holder<Attribute>, TriNum> attributeStable;//对于较稳定的attribute，附加至物品堆 TO STACK
    public final Map<Holder<Attribute>, TriNum> attributeActive;//对于较多变的attribute，直接附加至实体 TO ENTITY
    public final Map<ResourceLocation, TriNum> equipAttribute;
    public final Map<Class<Event>, Consumer<Event>> listeners;// TO ENTITY
    public final IntSet elementHashes;

    public PerformancePack(ResourceLocation[] eventPath,
                           Map<Holder<Attribute>, TriNum> attributeStable,
                           Map<Holder<Attribute>, TriNum> attributeActive,
                           Map<ResourceLocation, TriNum> equipAttribute,
                           Map<Class<Event>, Consumer<Event>> listeners,
                           IntSet elementHashes) {
        this.eventPath = eventPath;
        this.combinePath = combine(eventPath);
        this.attributeStable = attributeStable;
        this.attributeActive = attributeActive;
        this.equipAttribute = equipAttribute;
        this.listeners = listeners;
        this.elementHashes = elementHashes;
    }

    public void binding(LivingEntity entity) {

        attributeActive.forEach((atr, tri) -> {
            Optional.ofNullable(entity.getAttribute(atr)).ifPresent(ins -> {
                if (tri.v1() != 0)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(combinePath.withSuffix("/stage1"), tri.v1, AttributeModifier.Operation.ADD_VALUE));
                if (tri.v2() != 0)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(combinePath.withSuffix("/stage2"), tri.v2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
                if (tri.v3() != 1)
                    ins.addOrReplacePermanentModifier(new AttributeModifier(combinePath.withSuffix("/stage3"), tri.v3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            });
        });

        EventDistributor distribute = entity.getData(BreaHoriz.EVENT_DISTRIBUTOR.get());

        listeners.forEach((event, consumer) -> addEventListener(event, consumer, distribute));
    }

    private <T extends Event> void addEventListener(Class<T> clazz, Consumer<? extends Event> consumer, EventDistributor distribute) {
        distribute.add(clazz, (Consumer<T>) consumer, eventPath);
    }


    public void binding(ItemStack stack, EquipmentSlotGroup group, ItemAttributeModifiers.Builder builder) {
        //TODO 将原始内容转移至其它地方作为缓存 以在数据修改后可以叠加原始数据
        this.attributeStable.forEach((atr, tri) -> {
            if (tri.v1() != 0)
                builder.add(atr, new AttributeModifier(combinePath.withSuffix("/stage1"), tri.v1, AttributeModifier.Operation.ADD_VALUE), group);
            if (tri.v2() != 0)
                builder.add(atr, new AttributeModifier(combinePath.withSuffix("/stage2"), tri.v2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group);
            if (tri.v3() != 1)
                builder.add(atr, new AttributeModifier(combinePath.withSuffix("/stage3"), tri.v3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), group);
        });

        //eqiup attribute交给更上级自己处理
    }

    public void debind(LivingEntity entity) {
        attributeActive.keySet().stream().map(entity::getAttribute)
                .filter(Objects::nonNull)
                .forEach(ins -> {
                    ins.removeModifier(combinePath.withSuffix("/stage1"));
                    ins.removeModifier(combinePath.withSuffix("/stage2"));
                    ins.removeModifier(combinePath.withSuffix("/stage3"));
                });

        EventDistributor distribute = entity.getData(BreaHoriz.EVENT_DISTRIBUTOR.get());
        distribute.removeInPath(eventPath);
    }

    public static class Builder {
        public final ResourceLocation[] path;
        private List<PerformancePack> children = new ArrayList<>();

        public Builder(ResourceLocation[] path) {
            this.path = path;
        }

        public Builder addChild(PerformancePack child) {
            children.add(child);
            return this;
        }

        public Builder addChildren(Collection<PerformancePack> children) {
            this.children.addAll(children);
            return this;
        }

        public PerformancePack build() {
            Map<Holder<Attribute>, MutableTriNum> attributeStable = new HashMap<>();
            Map<Holder<Attribute>, MutableTriNum> attributeActive = new HashMap<>();
            Map<ResourceLocation, MutableTriNum> equipAttribute = new HashMap<>();
            Multimap<Class<Event>, Consumer<Event>> events = HashMultimap.create();
            IntSet elementHashes = new IntOpenHashSet();

            for (PerformancePack child : children) {
                child.attributeStable.forEach((key, value) -> {
                    attributeStable.computeIfAbsent(key, k -> new MutableTriNum()).add(value);
                });

                child.attributeActive.forEach((key, value) -> {
                    attributeActive.computeIfAbsent(key, k -> new MutableTriNum()).add(value);
                });

                child.equipAttribute.forEach((key, value) -> {
                    equipAttribute.computeIfAbsent(key, k -> new MutableTriNum()).add(value);
                });

                child.listeners.forEach(events::put);
                elementHashes.add(child.hashCode());
            }

            Map<Class<Event>, Consumer<Event>> listeners = new HashMap<>();
            events.keySet().forEach(key -> {
                listeners.put(key, e -> List.copyOf(events.get(key)).forEach(c -> c.accept(e)));
            });

            return new PerformancePack(path,
                    buildTriNum(attributeStable),
                    buildTriNum(attributeActive),
                    buildTriNum(equipAttribute),
                    ImmutableMap.copyOf(listeners),
                    IntSets.unmodifiable(elementHashes));
        }
    }

    public record TriNum(double v1, double v2, double v3) {

        public TriNum() {
            this(0, 0, 1);
        }

        public TriNum add1(double value) {
            return new TriNum(v1 + value, v2, v3);
        }

        public TriNum add2(double value) {
            return new TriNum(v1, v2 + value, v3);
        }

        public TriNum add3(double value) {
            return new TriNum(v1, v2, v3 * (1 + value));
        }

        public TriNum add(double v1, double v2, double v3) {
            return new TriNum(this.v1 + v1, this.v2 + v2, this.v3 * (1 + v3));
        }
    }

    public static class MutableTriNum {
        public double v1 = 0, v2 = 0, v3 = 1;

        public MutableTriNum add1(double value) {
            v1 += value;
            return this;
        }

        public MutableTriNum add2(double value) {
            v2 += value;
            return this;
        }

        public MutableTriNum add3(double value) {
            v3 *= (1 + value);
            return this;
        }

        public MutableTriNum add(double v1, double v2, double v3) {
            this.v1 += v1;
            this.v2 += v2;
            this.v3 *= (1 + v3);
            return this;
        }

        public MutableTriNum add(TriNum triNum) {
            return add(triNum.v1, triNum.v2, triNum.v3);
        }

        public TriNum build() {
            return new TriNum(this.v1, this.v2, this.v3);
        }
    }

    public static <T> Map<T, TriNum> buildTriNum(Map<T, MutableTriNum> origin) {
        ImmutableMap.Builder<T, TriNum> builder = ImmutableMap.builder();
        origin.forEach((key, value) -> builder.put(key, value.build()));
        return builder.build();
    }

    public static ResourceLocation combine(ResourceLocation[] rls) {
        if (rls.length == 0) return ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
        else if (rls.length == 1) return rls[0];
        else {
            ResourceLocation rl = rls[0];
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < rls.length; i++) {
                builder.append("/").append(rls[i].getNamespace()).append("/").append(rls[i].getPath());
            }
            return rl.withSuffix(builder.toString());
        }
    }

    public static ResourceLocation[] eventPath(ResourceLocation[] root, ResourceLocation... path) {
        if (path.length == 0) return root;
        ResourceLocation[] result = new ResourceLocation[root.length + path.length];
        System.arraycopy(root, 0, result, 0, root.length);
        System.arraycopy(path, 0, result, root.length, path.length);
        return result;
    }

    public static final ResourceLocation EQUIP_ROOT = ResourceLocation.withDefaultNamespace("equipment");

    public static final ResourceLocation[] AT_ANY = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("any")};
    public static final ResourceLocation[] AT_MAINHAND = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("mainhand")};
    public static final ResourceLocation[] AT_OFFHAND = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("offhand")};
    public static final ResourceLocation[] AT_HAND = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("hand")};
    public static final ResourceLocation[] AT_FEET = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("feet")};
    public static final ResourceLocation[] AT_LEGS = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("legs")};
    public static final ResourceLocation[] AT_CHEST = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("chest")};
    public static final ResourceLocation[] AT_HEAD = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("head")};
    public static final ResourceLocation[] AT_ARMOR = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("armor")};
    public static final ResourceLocation[] AT_BODY = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("body")};
    public static final ResourceLocation[] AT_SADDLE = new ResourceLocation[]{EQUIP_ROOT, ResourceLocation.withDefaultNamespace("saddle")};
}
