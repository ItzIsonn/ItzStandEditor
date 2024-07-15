package me.itzisonn_.itzstandeditor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.itzstandeditor.enums.CopyType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.HashMap;

@EqualsAndHashCode
public class InventoryData implements InventoryHolder {
    private final ItzStandEditor plugin;
    @Getter
    private ArmorStand stand;
    @Getter
    private int section;
    @Getter
    private final HashMap<CopyType, ArrayList<Object>> copyData = new HashMap<>();
    @Getter
    private Inventory inventory;

    public InventoryData(ItzStandEditor plugin, ArmorStand stand) {
        this.plugin = plugin;
        this.stand = stand;
        section = 0;
        inventory = plugin.getConfigManager().getInventory(this);
        updateInventory(false);
    }

    public void updateStand(ArmorStand stand) {
        this.stand = stand;
    }

    public void nextSection() {
        section++;

        if (section > plugin.getConfigManager().getSections().size() - 1) {
            section = 0;
        }
    }

    public void previousSection() {
        section--;

        if (section < 0) {
            section = plugin.getConfigManager().getSections().size() - 1;
        }
    }

    public void resetSection() {
        section = 0;
    }

    public void updateInventory(boolean b) {
        if (b) {
            inventory = plugin.getConfigManager().getInventory(this);
        }
        inventory.clear();
        plugin.getConfigManager().placeItems(this);
    }
}