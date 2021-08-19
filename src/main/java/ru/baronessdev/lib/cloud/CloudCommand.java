package ru.baronessdev.lib.cloud;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.baronessdev.lib.cloud.menu.CloudMenuManager;

public class CloudCommand implements CommandExecutor {

    private final String permission;
    private final String permissionMessage;

    protected CloudCommand(String permission, String permissionMessage) {
        this.permission = permission;
        this.permissionMessage = permissionMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p;
        try {
            p = (Player) sender;
        } catch (ClassCastException ignored) {
            return true;
        }

        if (!p.hasPermission(permission)) {
            p.sendMessage(permissionMessage);
            return true;
        }

        if (CloudMenuManager.handle(p)) {
            p.openInventory(CloudMenuManager.getMenu());
        }
        return true;
    }
}
