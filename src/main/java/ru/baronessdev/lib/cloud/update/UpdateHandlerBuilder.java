package ru.baronessdev.lib.cloud.update;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class UpdateHandlerBuilder {

    private final JavaPlugin plugin;
    private final UpdateHandler.Priority handlerPriority;
    private final Class<? extends Event>[] events;

    private TextComponent message = UpdateHandlerFactory.getDefaultMessage();
    private String method = "getPlayer";
    private EventPriority eventPriority = EventPriority.NORMAL;
    private String permission = UpdateHandlerFactory.getDefaultPermission();
    private boolean ignoreCancelled = true;
    private int delay = 4 * 20;

    @SuppressWarnings("unchecked")
    protected UpdateHandlerBuilder(JavaPlugin plugin, UpdateHandler.Priority handlerPriority, Class<? extends Event>... events) {
        this.plugin = plugin;
        this.handlerPriority = handlerPriority;
        this.events = events;
    }

    public UpdateHandlerBuilder setMessage(TextComponent s) {
        message = s;
        return this;
    }

    public UpdateHandlerBuilder setMethod(String s) {
        method = s;
        return this;
    }

    public UpdateHandlerBuilder setPermission(String s) {
        permission = s;
        return this;
    }

    public UpdateHandlerBuilder setEventPriority(EventPriority eventPriority) {
        this.eventPriority = eventPriority;
        return this;
    }

    public UpdateHandlerBuilder setIgnoreCancelled(boolean ignoreCancelled) {
        this.ignoreCancelled = ignoreCancelled;
        return this;
    }

    public UpdateHandlerBuilder setDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public UpdateHandler build() {
        return new UpdateHandler(handlerPriority, events, message, method, eventPriority, permission, ignoreCancelled, delay, plugin);
    }
}
