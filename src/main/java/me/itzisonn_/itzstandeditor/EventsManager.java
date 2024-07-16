package me.itzisonn_.itzstandeditor;

import me.itzisonn_.itzstandeditor.enums.AppearanceType;
import me.itzisonn_.itzstandeditor.enums.CopyType;
import me.itzisonn_.itzstandeditor.enums.RotationAxis;
import me.itzisonn_.itzstandeditor.enums.RotationType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class EventsManager implements Listener {
    private final ItzStandEditor plugin;

    public EventsManager(ItzStandEditor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.ARMOR_STAND) return;
        if (plugin.getConfigManager().getRequireShift() && !e.getPlayer().isSneaking()) return;
        if (!e.getPlayer().hasPermission("itzstandeditor.*") && !e.getPlayer().hasPermission("itzstandeditor.open")) return;

        e.setCancelled(true);
        plugin.getGuiManager().open(e.getPlayer(), (ArmorStand) e.getRightClicked());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!plugin.getGuiManager().getInventories().containsKey((Player) e.getPlayer())) return;
        plugin.getGuiManager().close((Player) e.getPlayer(), false);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.ARMOR_STAND) return;

        for (Player player : plugin.getGuiManager().getInventories().keySet()) {
            if (plugin.getGuiManager().getInventories().get(player).getStand() == e.getEntity()) {
                plugin.getGuiManager().close(player, true);
            }
        }
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (plugin.getGuiManager().getInventories().get((Player) e.getWhoClicked()) == null || e.getClickedInventory() == null) return;

        if (e.isShiftClick() && e.getClickedInventory() == e.getWhoClicked().getInventory() &&
                plugin.isInventory(plugin.getGuiManager().getInventories().get((Player) e.getWhoClicked()).getInventory(), e.getWhoClicked().getOpenInventory().getTopInventory())) {
            e.setCancelled(true);
            return;
        }

        if (!plugin.isInventory(e.getClickedInventory(),
                plugin.getGuiManager().getInventories().get((Player) e.getWhoClicked()).getInventory())) return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        List<String> functions = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(plugin.getNskFunctions(), PersistentDataType.LIST.strings());

        if (functions == null) return;

        Player player = (Player) e.getWhoClicked();
        InventoryData inventoryData = plugin.getGuiManager().getInventories().get(player);
        
        for (String function : functions) {
            String functionId = function.split("\\*")[0].toUpperCase();
            String clickType = function.split("\\*")[1];
            
            if ((clickType.equals("LEFT") && !e.isLeftClick()) || (clickType.equals("RIGHT") && !e.isRightClick())) continue;
            
            switch (functionId) {
                case "NAV_NEXT" -> inventoryData.nextSection();
                case "NAV_PREV" -> inventoryData.previousSection();

                case "ARMOR_CHEST", "ARMOR_LEGS", "ARMOR_FEET" -> {
                    if (plugin.getConfigManager().getBlockNotArmor() && e.getCursor().getType() != Material.AIR) {
                        switch (functionId) {
                            case "ARMOR_CHEST" -> {
                                if (!e.getCursor().getType().name().endsWith("CHESTPLATE")) return;
                            }
                            case "ARMOR_LEGS" -> {
                                if (!e.getCursor().getType().name().endsWith("LEGGINGS")) return;
                            }
                            case "ARMOR_FEET" -> {
                                if (!e.getCursor().getType().name().endsWith("BOOTS")) return;
                            }
                        }
                    }
                    ItemStack item = plugin.getArmorStandAPI().changeArmor(inventoryData.getStand(), EquipmentSlot.valueOf(functionId.split("_")[1]), e.getCursor());
                    e.getWhoClicked().setItemOnCursor(item);
                }

                case "ARMOR_HEAD", "ARMOR_HAND", "ARMOR_OFFHAND" -> {
                    ItemStack item = plugin.getArmorStandAPI().changeArmor(inventoryData.getStand(), 
                            EquipmentSlot.valueOf(functionId.split("_")[1].replaceAll("OFFHAND", "OFF_HAND")), e.getCursor());
                    e.getWhoClicked().setItemOnCursor(item);
                }

                case "BLOCK_HEAD", "BLOCK_CHEST", "BLOCK_LEGS", "BLOCK_FEET", "BLOCK_HAND" ->
                        plugin.getArmorStandAPI().toggleBlockArmorSlot(inventoryData.getStand(), 
                                EquipmentSlot.valueOf(functionId.split("_")[1]));

                case "APPEAR_PLATE", "APPEAR_ARMS", "APPEAR_INVISIBILITY", "APPEAR_GLOWING", "APPEAR_SMALL", "APPEAR_GRAVITY", "APPEAR_INVULNERABILITY" ->
                        plugin.getArmorStandAPI().changeAppearance(inventoryData.getStand(), AppearanceType.valueOf(functionId.split("_")[1]));

                case "ROTATE_HEAD_X_PLUS", "ROTATE_HEAD_X_MINUS", "ROTATE_HEAD_Y_PLUS", "ROTATE_HEAD_Y_MINUS", "ROTATE_HEAD_Z_PLUS", "ROTATE_HEAD_Z_MINUS", "ROTATE_HEAD_CLEAR",
                     "ROTATE_LEFTARM_X_PLUS", "ROTATE_LEFTARM_X_MINUS", "ROTATE_LEFTARM_Y_PLUS", "ROTATE_LEFTARM_Y_MINUS", "ROTATE_LEFTARM_Z_PLUS", "ROTATE_LEFTARM_Z_MINUS", "ROTATE_LEFTARM_CLEAR",
                     "ROTATE_RIGHTARM_X_PLUS", "ROTATE_RIGHTARM_X_MINUS", "ROTATE_RIGHTARM_Y_PLUS", "ROTATE_RIGHTARM_Y_MINUS", "ROTATE_RIGHTARM_Z_PLUS", "ROTATE_RIGHTARM_Z_MINUS", "ROTATE_RIGHTARM_CLEAR",
                     "ROTATE_LEFTLEG_X_PLUS", "ROTATE_LEFTLEG_X_MINUS", "ROTATE_LEFTLEG_Y_PLUS", "ROTATE_LEFTLEG_Y_MINUS", "ROTATE_LEFTLEG_Z_PLUS", "ROTATE_LEFTLEG_Z_MINUS", "ROTATE_LEFTLEG_CLEAR",
                     "ROTATE_RIGHTLEG_X_PLUS", "ROTATE_RIGHTLEG_X_MINUS", "ROTATE_RIGHTLEG_Y_PLUS", "ROTATE_RIGHTLEG_Y_MINUS", "ROTATE_RIGHTLEG_Z_PLUS", "ROTATE_RIGHTLEG_Z_MINUS", "ROTATE_RIGHTLEG_CLEAR" ->
                        plugin.getArmorStandAPI().changeRotation(inventoryData.getStand(), functionId.endsWith("MINUS"), e.isShiftClick(),
                                RotationType.valueOf(functionId.split("_")[1]), RotationAxis.valueOf(functionId.split("_")[2]));

                case "COPY_ROTATE_HEAD", "COPY_ROTATE_LEFTARM", "COPY_ROTATE_RIGHTARM", "COPY_ROTATE_LEFTLEG", "COPY_ROTATE_RIGHTLEG",
                     "COPY_APPEAR_PLATE", "COPY_APPEAR_ARMS", "COPY_APPEAR_INVISIBILITY", "COPY_APPEAR_GLOWING", "COPY_APPEAR_SMALL", "COPY_APPEAR_GRAVITY", "COPY_APPEAR_INVULNERABILITY",
                     "COPY_ARMOR_HEAD", "COPY_ARMOR_CHEST", "COPY_ARMOR_LEGS", "COPY_ARMOR_FEET", "COPY_ARMOR_HAND", "COPY_ARMOR_OFFHAND" ->
                        inventoryData.getCopyData().put(CopyType.valueOf(functionId.replaceAll("COPY_", "")),
                                plugin.getArmorStandAPI().copyData(inventoryData.getStand(), CopyType.valueOf(functionId.replaceAll("COPY_", ""))));

                case "PASTE_ROTATE_HEAD", "PASTE_ROTATE_LEFTARM", "PASTE_ROTATE_RIGHTARM", "PASTE_ROTATE_LEFTLEG", "PASTE_ROTATE_RIGHTLEG",
                     "PASTE_APPEAR_PLATE", "PASTE_APPEAR_ARMS", "PASTE_APPEAR_INVISIBILITY", "PASTE_APPEAR_GLOWING", "PASTE_APPEAR_SMALL", "PASTE_APPEAR_GRAVITY", "PASTE_APPEAR_INVULNERABILITY",
                     "PASTE_ARMOR_HEAD", "PASTE_ARMOR_CHEST", "PASTE_ARMOR_LEGS", "PASTE_ARMOR_FEET", "PASTE_ARMOR_HAND", "PASTE_ARMOR_OFFHAND" ->
                        plugin.getArmorStandAPI().pasteData(inventoryData.getStand(), CopyType.valueOf(functionId.replaceAll("PASTE_", "")),
                                inventoryData.getCopyData().get(CopyType.valueOf(functionId.replaceAll("PASTE_", ""))));

                case "CLEAR_ROTATE_HEAD", "CLEAR_ROTATE_LEFTARM", "CLEAR_ROTATE_RIGHTARM", "CLEAR_ROTATE_LEFTLEG", "CLEAR_ROTATE_RIGHTLEG",
                     "CLEAR_APPEAR_PLATE", "CLEAR_APPEAR_ARMS", "CLEAR_APPEAR_INVISIBILITY", "CLEAR_APPEAR_GLOWING", "CLEAR_APPEAR_SMALL", "CLEAR_APPEAR_GRAVITY", "CLEAR_APPEAR_INVULNERABILITY",
                     "CLEAR_ARMOR_HEAD", "CLEAR_ARMOR_CHEST", "CLEAR_ARMOR_LEGS", "CLEAR_ARMOR_FEET", "CLEAR_ARMOR_HAND", "CLEAR_ARMOR_OFFHAND" ->
                        inventoryData.getCopyData().remove(CopyType.valueOf(functionId.replaceAll("CLEAR_", "")));

                default -> {
                    if (functionId.matches("^MESSAGE:.*")) {
                        String message = function.split("\\*")[0].replaceAll("(?i)^MESSAGE:", "");
                        player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfigManager().parsePlaceholders(inventoryData, message)));
                    }

                    if (functionId.matches("^CONSOLE:.*")) {
                        String command = function.split("\\*")[0].replaceAll("(?i)^CONSOLE:", "").replaceAll("%player%", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfigManager().parsePlaceholders(inventoryData, command));
                    }

                    if (functionId.matches("^PLAYER:.*")) {
                        String command = function.split("\\*")[0].replaceAll("(?i)^PLAYER:", "").replaceAll("%player%", player.getName());
                        player.chat("/" + plugin.getConfigManager().parsePlaceholders(inventoryData, command));
                    }

                    if (functionId.matches("^CLOSE")) {
                        plugin.getGuiManager().close(player, true);
                        return;
                    }
                }
            }

            inventoryData.updateInventory(false);
        }
    }
}