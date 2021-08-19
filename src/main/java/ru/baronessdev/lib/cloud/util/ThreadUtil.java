package ru.baronessdev.lib.cloud.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unused")
public class ThreadUtil {

    public static void runOnBukkitThread(JavaPlugin plugin, Runnable r) {
        Bukkit.getScheduler().runTask(plugin, r);
    }

    public static void runAsyncThread(ThreadTask r) {
        new Thread(r::execute).start();
    }

    public static void runAsyncBukkit(JavaPlugin plugin, Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, r);
    }

    public static void runLaterSynchronously(JavaPlugin plugin, int t, Runnable r) {
        Bukkit.getScheduler().runTaskLater(plugin, r, t);
    }

    public static void runLaterAsynchronously(JavaPlugin plugin, int t, Runnable r) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, r, t);
    }

    public static void runTimerSynchronously(JavaPlugin plugin, int t, BukkitRunnable r) {
        r.runTaskTimer(plugin, 0, t);
    }

    public static void runTimerAsynchronously(JavaPlugin plugin, int t, BukkitRunnable r) {
        r.runTaskTimerAsynchronously(plugin, 0, t);
    }

    public interface ThreadTask {
        void execute();
    }
}
