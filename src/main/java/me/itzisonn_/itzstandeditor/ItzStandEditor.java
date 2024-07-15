package me.itzisonn_.itzstandeditor;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class ItzStandEditor extends JavaPlugin {
    private NamespacedKey nskFunctions;
    private ConfigManager configManager;
    private GuiManager guiManager;
    private ArmorStandAPI armorStandAPI;
    private boolean isHookedPapi = false;

    @Override
    public void onEnable() {
        nskFunctions = new NamespacedKey(this, "functions");
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        guiManager = new GuiManager(this);
        armorStandAPI = new ArmorStandAPI();
        new Command(this);

        Bukkit.getServer().getPluginManager().registerEvents(new EventsManager(this), this);
        if (configManager.isCrEnabled()) guiManager.startPlayParticles();

        if (configManager.getTryHookPapi()) {
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                Bukkit.getServer().getConsoleSender().sendMessage("[ItzStandEditor] Successfully hooked into PlaceholderAPI!");
                isHookedPapi = true;
            }
            else {
                Bukkit.getServer().getConsoleSender().sendMessage("[ItzStandEditor] Can't find PlaceholderAPI!");
            }
        }

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[ItzStandEditor] Enabled!"));
    }

    @Override
    public void onDisable() {
        if (configManager.isCrEnabled()) guiManager.stopPlayParticles();
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[ItzStandEditor] Disabled!"));
    }

    public boolean isInventory(Inventory inv1, Inventory inv2) {
        return inv1.getHolder() == inv2.getHolder();
    }
}