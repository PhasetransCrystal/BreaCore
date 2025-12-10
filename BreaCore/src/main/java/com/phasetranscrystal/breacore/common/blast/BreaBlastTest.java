package com.phasetranscrystal.breacore.common.blast;

import com.phasetranscrystal.breacore.api.blast.player.SkillGroup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class BreaBlastTest {

    @SubscribeEvent
    public static void testSkillInit(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SkillGroup data = serverPlayer.getData(BreaBlast.PLAYER_SKILL_GROUP);
            data.unlock(BreaBlast.TEST_SKILL);
            data.changeTo(BreaBlast.TEST_SKILL);
        }
    }

    // public static final DeferredHolder<Skill<?>, Skill<Player>> OLD_MA = SKILL.register("old_ma", () -> Skill.Builder
    // .<Player>of(50, 3, 0, 0, 50)
    // .start(data -> data.getEntity().displayClientMessage(Component.literal("OldMaInit"), false))
    // .judge((data, name) -> data.getCharge() == 3)
    // .addBehavior(builder -> builder
    // .onKeyInput((data, pack) -> data.getEntity().sendSystemMessage(Component.literal("按键拦截成功")), GLFW.GLFW_KEY_H)
    // .endWith(data -> data.getEntity().displayClientMessage(Component.literal("OldMaEnd"), false)),
    // "key test")
    // .build(Player.class));

    public static class Start extends Item {

        public Start() {
            super(new Properties());
        }

        @Override
        public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
            if (level instanceof ServerLevel server) {
                if (usedHand == InteractionHand.OFF_HAND && !player.getData(BreaBlast.PLAYER_SKILL_GROUP).getCurrentSkillData().isEnabled()) {
                    player.getData(BreaBlast.PLAYER_SKILL_GROUP).getCurrentSkillData().requestEnable();
                } else {
                    player.getData(BreaBlast.PLAYER_SKILL_GROUP).getCurrentSkillData().switchToIfNot("active");
                }
            }
            return InteractionResult.SUCCESS;
        }
    }
}
