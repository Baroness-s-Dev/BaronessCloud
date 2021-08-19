package ru.baronessdev.lib.cloud;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.baronessdev.lib.cloud.linked.LinkedPlugin;
import ru.baronessdev.lib.cloud.linked.LinkedPluginFactory;
import ru.baronessdev.lib.cloud.log.Logger;
import ru.baronessdev.lib.cloud.menu.CloudMenuManager;
import ru.baronessdev.lib.cloud.update.UpdateHandler;
import ru.baronessdev.lib.cloud.update.UpdateHandlerAction;
import ru.baronessdev.lib.cloud.update.UpdateHandlerFactory;
import ru.baronessdev.lib.cloud.update.UpdateHandlerListener;
import ru.baronessdev.lib.cloud.update.checker.UpdateCheckException;
import ru.baronessdev.lib.cloud.update.checker.UpdateCheckerUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaronessCloud extends JavaPlugin {

    private static BaronessCloud instance;

    @SuppressWarnings("unused")
    public static BaronessCloud getInstance() {
        return instance;
    }


    private FileConfiguration config;
    private boolean disabled;

    private final List<Index> indexList = new ArrayList<>();
    private final List<LinkedPlugin> pluginList = new ArrayList<>();
    private final List<JavaPlugin> alreadyAdded = new ArrayList<>();

    private static int updateCount = 0;
    private UpdateHandler currentHandler;

    @Override
    public void onEnable() {
        instance = this;

        setupConfig();
        UpdateHandlerFactory.setConfig(config);
        if (isDisabled()) return;

        setupIndexes();
        setupCommand();
    }

    private void setupConfig() {
        saveDefaultConfig();
        config = getConfig();
        disabled = !config.getBoolean("enabled");
    }

    private void setupIndexes() {
        // downloading indexes
        File indexesFile = new File("plugins" + File.separator + "BaronessCloud" + File.separator + "indexes");
        try {
            Files.copy(new URL("https://mirror.baronessdev.ru/BaronessCloud/indexes.yml").openStream(), indexesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(ChatColor.RED + "BaronessCloud could not download indexes: " + e.getMessage());
            disabled = true;
            return;
        }

        // loading indexes
        YamlConfiguration indexConfig = YamlConfiguration.loadConfiguration(indexesFile);
        indexConfig.getKeys(false).forEach(key -> indexList.add(new Index(
                key,
                indexConfig.getString(key + ".url"),
                indexConfig.getString(key + ".changelogs"),
                indexConfig.getStringList(key + ".description"),
                indexConfig.getString(key + ".material"),
                indexConfig.getString(key + ".fallbackMaterial"),
                indexConfig.getStringList(key + ".depends"),
                indexConfig.getStringList(key + ".sync")
        )));

        //noinspection ResultOfMethodCallIgnored
        indexesFile.delete();

        buildMenu();
    }

    private void setupCommand() {
        getCommand("baronesscloud").setExecutor(new CloudCommand(
                config.getString("command.permission"),
                config.getString("command.no-permission-message")
        ));
    }

    @SuppressWarnings("unused")
    public synchronized BaronessCloud addPlugin(JavaPlugin plugin) {
        if (isDisabled()) return this;
        String pluginName = plugin.getName();

        // prevents double usage from same plugin
        if (alreadyAdded.contains(plugin)) return this;

        // checks whether the plugin is in the indexes
        Index index = indexList.stream().filter(in -> in.getName().equals(pluginName)).findFirst().orElse(null);
        Validate.notNull(index, pluginName + " is not in BaronessCloud indexes");

        alreadyAdded.add(plugin);

        UpdateCheckerUtil.checkAsynchronously(index.getUrl(), (latestVersion) -> {

            // adding plugin to linked plugin list after collecting latest version data
            pluginList.add(LinkedPluginFactory.create(index, plugin, latestVersion, config));
            indexList.remove(index);

            buildMenu();

        }, this::logVersionCheckException);
        return this;
    }

    @SuppressWarnings({"unused"})
    public synchronized void addUpdateHandler(JavaPlugin plugin, UpdateHandler handler) {
        if (isDisabled()) return;

        if (currentHandler != null && handler.getHandlerPriority().higher(currentHandler.getHandlerPriority()))
            return;

        UpdateHandlerListener listener = new UpdateHandlerListener();
        handler.setListener(listener);
        Arrays.stream(handler.getEvents()).forEach(clazz -> Bukkit.getPluginManager().registerEvent(
                clazz,
                listener,
                handler.getEventPriority(),
                (l, event) -> UpdateHandlerAction.defaultAction(event, handler),
                plugin,
                handler.isIgnoreCancelled()
        ));

        if (currentHandler != null) currentHandler.deactivate();
        currentHandler = handler;
    }


    private void logVersionCheckException(UpdateCheckException e) {
        UpdateCheckerUtil.logDefaultExceptionError(new Logger.Builder("BaronessCloud").build(), e);
    }

    private void buildMenu() {
        CloudMenuManager.build(new ArrayList<>(pluginList), new ArrayList<>(indexList), config);
    }

    private boolean isDisabled() {
        return disabled;
    }

    public static void setUpdateCount(int updateCount) {
        BaronessCloud.updateCount = updateCount;
    }

    public static int getUpdateCount() {
        return updateCount;
    }
}
