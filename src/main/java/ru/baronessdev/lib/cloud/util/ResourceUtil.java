package ru.baronessdev.lib.cloud.util;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.logging.Level;

public class ResourceUtil {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveResource(@NotNull JavaPlugin plugin, @NotNull String resourcePath, @NotNull File outFile, boolean replace) {
        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        Validate.notNull(in, "Resource " + resourcePath + " is null");

        outFile.getParentFile().mkdirs();
        if (outFile.exists() && !replace) return;

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }
}
