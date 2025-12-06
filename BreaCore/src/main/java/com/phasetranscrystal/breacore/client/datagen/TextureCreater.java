package com.phasetranscrystal.breacore.client.datagen;

import com.phasetranscrystal.breacore.api.item.TagPrefixItem;

import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.item.Item;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.generators.RegistrateItemModelGenerator;

public class TextureCreater {

    public static void init() {
    }

    public static void generageTagPrefixItemModel(DataGenContext<Item, TagPrefixItem> ctx, RegistrateItemModelGenerator prov) {
        var item = ctx.getEntry();
        var mat = item.material;
        var tagPrefix = item.tagPrefix;
        var iconSet = mat.getMaterialIconSet();
        iconSet = iconSet.isRootIconset ? iconSet : iconSet.parentIconset;
        var iconType = tagPrefix.materialIconType();
        var sourceIcon = iconType.getItemTexturePath(iconSet, true);
        if (sourceIcon == null) return;
        var mapping = new TextureMapping()
                .put(TextureSlot.LAYER0, sourceIcon);
        var mrl = ModelTemplates.FLAT_ITEM.create(item, mapping, (l, m) -> {
            prov.modelOutput.accept(l, m);
        });
        prov.createWithExistingModel(ctx.getEntry(), mrl);
    }
}
