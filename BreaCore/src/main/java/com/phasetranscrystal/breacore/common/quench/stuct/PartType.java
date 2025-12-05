package com.phasetranscrystal.breacore.common.quench.stuct;

import com.phasetranscrystal.breacore.api.material.property.PropertyKey;
import com.phasetranscrystal.breacore.api.registry.BreaRegistries;
import com.phasetranscrystal.breacore.api.tag.TagPrefix;
import com.phasetranscrystal.breacore.common.quench.data.IMaterialQuenchData;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

/**
 * 部件类型
 * <p>
 * 部件类型是制造锻造装备的物品类型单元，可以被视为对{@link TagPrefix}的再包装。
 * 其指定了一个物品类型为此部件类型，并指明了如何获取到该部件的数值信息。
 *
 * @param requiredType 将该物品类型指定为此部件类型
 * @param finderKey    该部件类型被锻造至装备后，对应的词条将从此处获取。另外，词条数值也将从此获取
 * @see IMaterialQuenchData 为材料类型准备的存储词条与部件信息的组件。
 */
public record PartType(TagPrefix requiredType, PropertyKey<? extends IMaterialQuenchData> finderKey) {

    public ResourceLocation getId() {
        return BreaRegistries.PART_TYPE.getKey(this);
    }

    @Override
    public @NotNull String toString() {
        return "PartType(" + getId() + ")";
    }
}
