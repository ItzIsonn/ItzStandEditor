package me.itzisonn_.itzstandeditor;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class GuiManager {
    private final ItzStandEditor plugin;
    @Getter
    private final HashMap<Player, InventoryData> inventories = new HashMap<>();
    private int taskId;
    private Player queuePlayer;

    public GuiManager(ItzStandEditor plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, ArmorStand stand) {
        InventoryData inventoryData;
        queuePlayer = player;

        if (inventories.get(player) != null) {
            inventoryData = inventories.get(player);
            inventoryData.updateStand(stand);
            inventoryData.updateInventory(true);
            player.openInventory(inventoryData.getInventory());
            return;
        }

        inventoryData = new InventoryData(plugin, stand);
        player.openInventory(inventoryData.getInventory());

        inventories.put(player, inventoryData);

        queuePlayer = null;
    }

    public void close(Player player, boolean b) {
        if (!inventories.containsKey(player)) return;
        if (!plugin.getConfigManager().getSaveSection()) inventories.get(player).resetSection();
        if (b) player.closeInventory();
        inventories.get(player).updateStand(null);
    }

    public Player getPlayer(InventoryData data) {
        for (Player player : inventories.keySet()) {
            if (inventories.get(player) == data) return player;
        }

        return queuePlayer;
    }

    public void startPlayParticles() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : inventories.keySet()) {
                if (inventories.get(player).getStand() == null) continue;

                Location start = player.getLocation().add(new Vector(0, 1, 0));
                Location end = inventories.get(player).getStand().getLocation().add(new Vector(0, 1, 0));

                double period = plugin.getConfigManager().getCrPeriod();
                Location current = start.clone();
                double distance = start.distance(end);
                int stepCount = (int) (distance / period);
                Vector step = new Vector((end.x() - start.x()) / stepCount, (end.y() - start.y()) / stepCount,(end.z() - start.z()) / stepCount);
                while (stepCount --> 0) {
                    current.getWorld().spawnParticle(plugin.getConfigManager().getCrParticle(), current,
                            plugin.getConfigManager().getCrAmount(), 0, 0, 0, 0);
                    current.add(step);
                }
            }
        }, 0, plugin.getConfigManager().getCrRate());
    }

    public void stopPlayParticles() {
        Bukkit.getScheduler().cancelTask(taskId);
    }
}