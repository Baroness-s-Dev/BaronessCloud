package ru.baronessdev.lib.cloud.update;

import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class UpdateHandler {
    private final UpdateHandler.Priority handlerPriority;
    private final Class<? extends Event>[] events;
    private final TextComponent message;
    private final String method;
    private final EventPriority eventPriority;
    private final String permission;
    private final boolean ignoreCancelled;
    private final int delay;
    private final JavaPlugin plugin;

    private Listener listener;

    public void deactivate() {
        HandlerList.unregisterAll(listener);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public enum Priority {
        DEFAULT(1),
        STANDARD(2),
        MEDIUM(3),
        HIGH(4),
        MAXIMUM(5);

        private final int power;

        Priority(int power) {
            this.power = power;
        }

        public int getPower() {
            return power;
        }

        public boolean higher(UpdateHandler.Priority other) {
            return other.getPower() >= power;
        }
    }

    public static class Builder {

        private final JavaPlugin plugin;
        private final UpdateHandler.Priority handlerPriority;
        private final Class<? extends Event>[] events;

        private TextComponent message = UpdateHandlerFactory.getDefaultMessage();
        private String method = "getPlayer";
        private EventPriority eventPriority = EventPriority.NORMAL;
        private String permission = UpdateHandlerFactory.getDefaultPermission();
        private boolean ignoreCancelled = true;
        private int delay = 4 * 20;

        @SafeVarargs
        public Builder(JavaPlugin plugin, UpdateHandler.Priority handlerPriority, Class<? extends Event>... events) {
            this.plugin = plugin;
            this.handlerPriority = handlerPriority;
            this.events = events;
        }

        public UpdateHandler.Builder setMessage(TextComponent s) {
            message = s;
            return this;
        }

        public UpdateHandler.Builder setMethod(String s) {
            method = s;
            return this;
        }

        public UpdateHandler.Builder setPermission(String s) {
            permission = s;
            return this;
        }

        public UpdateHandler.Builder setEventPriority(EventPriority eventPriority) {
            this.eventPriority = eventPriority;
            return this;
        }

        public UpdateHandler.Builder setIgnoreCancelled(boolean ignoreCancelled) {
            this.ignoreCancelled = ignoreCancelled;
            return this;
        }

        public UpdateHandler.Builder setDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public UpdateHandler build() {
            return new UpdateHandler(handlerPriority, events, message, method, eventPriority, permission, ignoreCancelled, delay, plugin);
        }
    }
}