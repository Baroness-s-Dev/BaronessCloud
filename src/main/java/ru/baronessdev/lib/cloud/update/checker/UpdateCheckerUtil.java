package ru.baronessdev.lib.cloud.update.checker;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.baronessdev.lib.cloud.log.LogLevel;
import ru.baronessdev.lib.cloud.log.Logger;
import ru.baronessdev.lib.cloud.util.ThreadUtil;

import java.io.IOException;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class UpdateCheckerUtil {

    public static void checkAsynchronously(@NotNull JavaPlugin plugin, @NotNull String url, @NotNull Logger logger) {
        ThreadUtil.runAsyncThread(() -> {
            try {
                check(plugin, url, logger);
            } catch (UpdateCheckException ignored) {
            }
        });
    }

    public static void checkAsynchronously(@NotNull String url, Consumer<String> acceptConsumer, Consumer<UpdateCheckException> exceptionConsumer) {
        ThreadUtil.runAsyncThread(() -> {
            String latest = "-1";
            try {
                latest = getLatest(url);
            } catch (UpdateCheckException e) {
                exceptionConsumer.accept(e);
            } finally {
                acceptConsumer.accept(latest);
            }
        });
    }

    public static String check(@NotNull JavaPlugin plugin, @NotNull String url, Logger logger) throws UpdateCheckException {
        if (logger != null) {
            return checkWithLogger(plugin, url, logger);
        }

        String latest = getLatest(url);
        if (latest.equals("-1")) {
            throw new UpdateCheckException("Checker returned -1");
        }

        return latest;
    }

    private static String checkWithLogger(JavaPlugin plugin, String url, Logger logger) {
        String latest = "-1";
        try {
            latest = UpdateCheckerUtil.check(plugin, url, null);
            if (!latest.equals("-1")) {
                logDefaultUpdate(logger, plugin, latest);
            }
        } catch (UpdateCheckException e) {
            logDefaultExceptionError(logger, e);
        }
        return latest;
    }

    public static void logDefaultUpdate(Logger logger, JavaPlugin plugin, String i) {
        logger.log(LogLevel.INFO, "New version found: v" + ChatColor.YELLOW + i + ChatColor.GRAY + " (Current: v" + plugin.getDescription().getVersion() + ")");
        logger.log(LogLevel.INFO, "Update now: " + ChatColor.AQUA + "market.baronessdev.ru");
    }

    public static void logDefaultExceptionError(Logger logger, UpdateCheckException e) {
        logger.log(LogLevel.ERROR, "Could not check for updates: " + e.getRootCause());
        logger.log(LogLevel.ERROR, "Please contact Baroness's Dev if this isn't your mistake.");
    }

    private static String getLatest(String url) throws UpdateCheckException {
        String version = "-1";
        try {
            String result = EntityUtils.toString(HttpClients.createDefault().execute(
                    new HttpGet(url)
            ).getEntity());

            for (String s : result.split("\n")) {
                if (s.contains("model")) {
                    s = s.split("\"")[3];
                    if (s.charAt(0) == 'v') {
                        s = s.substring(1);
                    }
                    version = s;
                    break;
                }
            }
        } catch (IOException e) {
            throw new UpdateCheckException(e.getCause().getMessage());
        }

        return version;
    }

    public static @NotNull UpdateType getUpdateType(@NotNull JavaPlugin plugin, String currentVersion, String latestVersion) {
        if (latestVersion.equals("-1")) return UpdateType.FAILED;
        if (!currentVersion.equals(latestVersion)) return UpdateType.AVAILABLE;
        return UpdateType.UNAVAILABLE;
    }

    public enum UpdateType {
        AVAILABLE,
        UNAVAILABLE,
        FAILED
    }
}
