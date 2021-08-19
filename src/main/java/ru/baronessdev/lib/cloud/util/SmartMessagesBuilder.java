package ru.baronessdev.lib.cloud.util;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class SmartMessagesBuilder {

    @Getter
    private final TextComponent message;

    public SmartMessagesBuilder(String message) {
        this.message = new TextComponent(message);
    }

    public SmartMessagesBuilder(TextComponent message) {
        this.message = message;
    }

    public SmartMessagesBuilder setHoverEvent(HoverEvent.Action action, String value) {
        message.setHoverEvent(new HoverEvent(action, new ComponentBuilder(value).create()));
        return this;
    }

    public SmartMessagesBuilder setClickEvent(ClickEvent.Action action, String value) {
        message.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public void send(Player p) {
        p.spigot().sendMessage(message);
    }
}
