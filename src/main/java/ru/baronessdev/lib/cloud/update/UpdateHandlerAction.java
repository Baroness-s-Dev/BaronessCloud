package ru.baronessdev.lib.cloud.update;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import ru.baronessdev.lib.cloud.BaronessCloud;
import ru.baronessdev.lib.cloud.util.SmartMessagesBuilder;
import ru.baronessdev.lib.cloud.util.ThreadUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

public class UpdateHandlerAction {

    public static void defaultAction(Event event, UpdateHandler handler) {
        int updatesCount = BaronessCloud.getUpdateCount();
        if (updatesCount == 0) {
            handler.deactivate();
            return;
        }

        // getting player via reflection
        Player p;
        try {
            Method method = Arrays.stream(event.getClass().getMethods())
                    .filter(m -> m.getName().equals(handler.getMethod()))
                    .findFirst().orElse(null);

            if (method == null) {
                return;
            }

            p = (Player) method.invoke(event);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!p.hasPermission(handler.getPermission())) return;
        ThreadUtil.runLaterSynchronously(handler.getPlugin(), handler.getDelay(), () -> {
            TextComponent textComponent = handler.getMessage();
            textComponent.setText(handler.getMessage().getText().replace("{count}", String.valueOf(updatesCount)));
            new SmartMessagesBuilder(textComponent).send(p);
        });

    }
}
