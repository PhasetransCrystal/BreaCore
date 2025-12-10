package com.phasetranscrystal.breacore.api.horiz;

import net.neoforged.bus.api.Event;

import java.util.Objects;
import java.util.function.Consumer;

public record EventIdent<T extends Event>(Class<T> event, Consumer<T> listener, boolean handleCancelled) {

    public EventIdent(Class<T> event, Consumer<T> listener) {
        this(event, listener, false);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventIdent<?> that = (EventIdent<?>) o;
        return Objects.equals(event, that.event) && Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, listener);
    }
}
