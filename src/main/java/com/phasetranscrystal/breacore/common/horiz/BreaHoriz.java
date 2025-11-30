package com.phasetranscrystal.breacore.common.horiz;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import com.tterrag.registrate.util.entry.RegistryEntry;

import static com.phasetranscrystal.breacore.api.registry.registry.BreaRegistrate.Brea;

public class BreaHoriz {

    public static void bootstrap() {
        EntityDistributorInit.bootstrapConsumer();
    }

    public static final RegistryEntry<AttachmentType<?>, AttachmentType<EventDistributor>> EVENT_DISTRIBUTOR;
    static {
        EVENT_DISTRIBUTOR = Brea.simple("horiz/event_distributor",
                NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                () -> AttachmentType.builder(holder -> new EventDistributor()).serialize(EventDistributor.CODEC).build());
    }
}
