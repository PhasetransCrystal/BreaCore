package com.phasetranscrystal.breacore.api.block.debug;

import com.phasetranscrystal.breacore.api.material.ChemicalHelper;
import com.phasetranscrystal.breacore.api.material.Material;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

public class CheckMatBlock extends Block {

    public CheckMatBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        displayMaterial(player, ChemicalHelper.getMaterialStack(stack).material(), "Material Stack:%s");
        var item = stack.getItem();
        displayMaterial(player, ChemicalHelper.getMaterialStack(item).material(), "Material Item:%s");
        return InteractionResult.SUCCESS;
    }

    private void displayMaterial(Player player, Material material, String format) {
        player.displayClientMessage(Component.literal(format.formatted(material.getName())), true);
    }
}
