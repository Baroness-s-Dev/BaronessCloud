package ru.baronessdev.lib.cloud.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ForkJoinPool;

public class ThreadUtil {

    public static void runAsyncThread(Runnable r) {
        ForkJoinPool.commonPool().execute(r);
    }

    public static void runLaterSynchronously(JavaPlugin plugin, int t, Runnable r) {
        Bukkit.getScheduler().runTaskLater(plugin, r, t);
    }
}
