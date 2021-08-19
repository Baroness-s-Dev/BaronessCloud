package ru.baronessdev.lib.cloud.linked;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Data
public class LinkedPlugin {

    private final JavaPlugin basePlugin;
    private final ItemStack icon;
    private final String url;
    private final boolean hasUpdate;
    private final List<String> baseDescription;

}
