package ru.baronessdev.lib.cloud.menu;

import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import ru.baronessdev.lib.cloud.BaronessCloud;
import ru.baronessdev.lib.cloud.Index;
import ru.baronessdev.lib.cloud.linked.LinkedPlugin;
import ru.baronessdev.lib.cloud.util.ItemBuilder;
import ru.baronessdev.lib.cloud.util.SmartMessagesBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CloudMenuManager {

    private static Inventory menu;
    private static final List<MenuListener> listeners = new ArrayList<>();

    private static FileConfiguration config;
    private static final LinkedHashMap<Integer, PluginUrl> keys = new LinkedHashMap<>();

    public static synchronized void build(List<LinkedPlugin> pluginList, List<Index> rawIndexList, FileConfiguration config) {
        CloudMenuManager.config = config;

        keys.clear();

        List<Index> indexList = new ArrayList<>();
        for (Index rawIndex : rawIndexList) {
            if (rawIndex.getDepends().stream().allMatch(dependPlugin -> Bukkit.getPluginManager().getPlugin(dependPlugin) != null)) {
                indexList.add(rawIndex);
            }
        }

        int size = 9;
        while (pluginList.size() + indexList.size() + 1 > size) size = size + 9;

        menu = Bukkit.createInventory(null, size, config.getString("menu.title"));
        AtomicInteger i = new AtomicInteger();

        // adding loaded plugins
        pluginList.forEach(linkedPlugin -> {
            menu.setItem(i.get(), linkedPlugin.getIcon());

            keys.put(i.getAndIncrement(), new PluginUrl(linkedPlugin.getUrl(), linkedPlugin.getVersionsUrl()));
        });

        // adding other plugins
        indexList.forEach(index -> {
            // description
            List<String> lore = new ArrayList<>(index.getDescription());
            lore.add("");

            // depends
            if (!index.getDepends().isEmpty()) {
                lore.add(config.getString("icon.depends"));
                for (String dependPlugin : index.getDepends())
                    lore.add(ChatColor.WHITE + " - " + ChatColor.YELLOW + dependPlugin);

                lore.add("");
            }

            // not installed
            lore.add(config.getString("icon.not-installed"));
            lore.add("");

            // link
            lore.add(config.getString("icon.link"));

            // version history
            lore.add(config.getString("icon.versions"));

            menu.setItem(i.get(), new ItemBuilder(Material.BARRIER)
                    .setName(ChatColor.GOLD + index.getName())
                    .setLore(lore)
                    .build()
            );

            keys.put(i.getAndIncrement(), new PluginUrl(index.getUrl(), index.getChangelogsUrl()));
        });

        // help button
        if (config.getBoolean("menu.help.enabled")) {
            menu.setItem(menu.getSize() - 1, new ItemBuilder(Material.getMaterial(config.getString("menu.help.material")))
                    .setName(config.getString("menu.help.name"))
                    .setLore(config.getStringList("menu.help.lore"))
                    .build()
            );
        }
    }

    public static synchronized void handle(Player p) {
        // deactivating old one (for safety)
        listeners.stream().filter(menuListener -> menuListener.player.equals(p)).findFirst().ifPresent(MenuListener::deactivate);

        MenuListener listener = new MenuListener(p);
        Bukkit.getPluginManager().registerEvents(listener, BaronessCloud.getInstance());
        listeners.add(listener);
    }

    public static Inventory getMenu() {
        return menu;
    }

    private static class MenuListener implements Listener {

        private final Player player;

        private MenuListener(Player player) {
            this.player = player;
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getCurrentItem() == null) return;

            Player p = (Player) e.getWhoClicked();
            if (!player.equals(p)) return; // another handler

            Inventory topInventory = p.getOpenInventory().getTopInventory();
            if (topInventory == null) return;
            if (!topInventory.equals(menu)) return;

            e.setCancelled(true);

            Inventory clickedInventory = e.getClickedInventory();
            if (clickedInventory == null) return;
            if (!clickedInventory.equals(menu)) return;

            p.closeInventory();
            deactivate();
            new SmartMessagesBuilder(config.getString("icon.click-me"))
                    .setClickEvent(ClickEvent.Action.OPEN_URL, keys.getOrDefault(e.getSlot(), new PluginUrl(
                            "https://market.baronessdev.ru/", "https://market.baronessdev.ru/")).getURLByClick(e.getClick()))
                    .send(p);
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClose(InventoryCloseEvent e) {
            Player p = (Player) e.getPlayer();
            if (!player.equals(p)) return; // another handler
            deactivate();
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent e) {
            deactivate();
        }

        protected void deactivate() {
            HandlerList.unregisterAll(this);
        }
    }

    @Data
    private static class PluginUrl {

        private final String url;
        private final String versionsUrl;

        public String getURLByClick(ClickType type) {
            if (type == ClickType.RIGHT) return versionsUrl;
            return url;
        }
    }
}
