package com.phasetranscrystal.breacore.data.blocks;

import com.phasetranscrystal.breacore.api.block.debug.CheckMatBlock;
import com.phasetranscrystal.breacore.api.block.debug.MuiTestBlock;
import com.phasetranscrystal.breacore.data.misc.BreaCreativeModeTabs;

import static com.phasetranscrystal.breacore.common.registry.BreaRegistration.REGISTRATE;
import static com.phasetranscrystal.breacore.data.blocks.BreaBlocks.*;

public class DebugBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> BreaCreativeModeTabs.DEBUG_ITEMS);
    }

    public static void init() {
        MatCheckBlock = REGISTRATE.block("matcheckblock", CheckMatBlock::new)
                .simpleItem()
                .lang("Material Check Block")
                .register();
        TestMuiBlock = REGISTRATE.block("mui_test_block", MuiTestBlock::new)
                .simpleItem()
                .lang("MUI Test Block")
                .register();
    }
}
