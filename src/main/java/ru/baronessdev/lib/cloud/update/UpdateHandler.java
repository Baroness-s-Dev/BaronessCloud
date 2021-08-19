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
}