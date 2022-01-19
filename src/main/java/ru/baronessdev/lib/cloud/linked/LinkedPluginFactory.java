package ru.baronessdev.lib.cloud.linked;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.baronessdev.lib.cloud.BaronessCloud;
import ru.baronessdev.lib.cloud.Index;
import ru.baronessdev.lib.cloud.log.Logger;
import ru.baronessdev.lib.cloud.update.checker.UpdateCheckerUtil;
import ru.baronessdev.lib.cloud.util.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class LinkedPluginFactory {

    public static LinkedPlugin create(@NotNull Index index, @NotNull JavaPlugin plugin, String latestVersion, @NotNull FileConfiguration config) {
        // getting current version
        final String currentVersion = plugin.getDescription().getVersion();

        // checking for updates
        UpdateCheckerUtil.UpdateType updateType = UpdateCheckerUtil.getUpdateType(plugin, currentVersion, latestVersion);
        final boolean hasUpdate = updateType == UpdateCheckerUtil.UpdateType.AVAILABLE;

        // material
        Material baseMaterial = Material.getMaterial(index.getMaterial());
        Material material = (baseMaterial == null) ? Material.getMaterial(index.getFallbackMaterial()) : baseMaterial;

        // name
        String name = ChatColor.GOLD + index.getName();

        // lore

        // description
        List<String> lore = new ArrayList<>(index.getDescription());
        lore.add("");

        // sync
        if (!index.getSync().isEmpty()) {
            lore.add(config.getString("icon.sync"));
            for (String syncedPlugin : index.getSync()) {
                ChatColor color = (Bukkit.getPluginManager().getPlugin(syncedPlugin) != null)
                        ? ChatColor.YELLOW
                        : ChatColor.GRAY;
                lore.add(ChatColor.WHITE + " - " + color + syncedPlugin);
            }
            lore.add("");
        }

        // versions
        lore.add(config.getString("icon.current-version").replace("%d", currentVersion));
        lore.add(config.getString("icon.actual-version").replace("%d", latestVersion));
        lore.add("");

        // update status
        lore.add(getUpdateStatusText(updateType, config));
        lore.add("");

        // link
        lore.add(config.getString("icon.link"));

        // version history
        lore.add(config.getString("icon.versions"));

        // building ItemStack
        ItemStack itemStack = new ItemBuilder(material)
                .setName(name)
                .setLore(lore)
                .build();

        if (hasUpdate) {
            BaronessCloud.setUpdateCount(BaronessCloud.getUpdateCount() + 1);
            UpdateCheckerUtil.logDefaultUpdate(new Logger.Builder("[" + plugin.getName() + "]").build(), plugin, latestVersion);
        }

        return new LinkedPlugin(plugin, itemStack, index.getUrl(), index.getChangelogsUrl(), hasUpdate, index.getDescription());
    }

    private static String getUpdateStatusText(@NotNull UpdateCheckerUtil.UpdateType updateType, @NotNull FileConfiguration config) {
        switch (updateType) {
            case AVAILABLE:
                return config.getString("icon.update-available");
            case UNAVAILABLE:
                return config.getString("icon.update-unavailable");
            case FAILED:
                return config.getString("icon.update-failed");
            default:
                return "";
        }
    }
}
