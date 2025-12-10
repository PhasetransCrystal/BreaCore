package com.phasetranscrystal.breacore.common.horiz;

import com.phasetranscrystal.breacore.api.annotation.BreaModule;
import com.phasetranscrystal.breacore.api.horiz.EventDistributor;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.phasetranscrystal.breacore.common.registry.BreaRegistration.REGISTRATE;

@BreaModule(moduleId = BreaHoriz.MODULE_ID, moduleName = BreaHoriz.MODULE_NAME)
public class BreaHoriz {

    public static final String MODULE_ID = "horiz";
    public static final String MODULE_NAME = "Horiz";

    public static void bootstrap() {
        EntityDistributorInit.bootstrapConsumer();
    }

    public static final RegistryEntry<AttachmentType<?>, AttachmentType<EventDistributor>> EVENT_DISTRIBUTOR;

    static {
        EVENT_DISTRIBUTOR = REGISTRATE.simple("horiz/event_distributor",
                NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                () -> AttachmentType.builder(holder -> new EventDistributor()).serialize(EventDistributor.CODEC).build());
    }
}
