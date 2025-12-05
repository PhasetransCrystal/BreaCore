package com.phasetranscrystal.breacore.common.quench.data;

import com.phasetranscrystal.breacore.api.material.property.IMaterialProperty;
import com.phasetranscrystal.breacore.api.material.property.MaterialProperties;
import com.phasetranscrystal.breacore.common.quench.IValueElemProvider;
import com.phasetranscrystal.breacore.common.quench.perk.Perk;
import com.phasetranscrystal.breacore.common.quench.stuct.PartType;

import java.util.Map;

/**
 * 为材料准备的锻造信息组件。
 * <p>
 * 存储了词条组与部件对应的数值。通常而言，我们将部件划分为三个大类：近程，远程，护甲。分别存储在三个材料属性组件内。
 */
public interface IMaterialQuenchData extends IMaterialProperty {

    /**
     * 获取该材料对应的词条组和默认强度。
     */
    Map<Perk, Double> getPerksAndStrength();

    /**
     * 通过部件类型获取数值组
     */
    IValueElemProvider getValues(PartType type);

    @Override
    default void verifyProperty(MaterialProperties properties) {}
}
