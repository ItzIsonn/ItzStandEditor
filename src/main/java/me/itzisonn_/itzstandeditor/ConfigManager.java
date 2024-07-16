package me.itzisonn_.itzstandeditor;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private final ItzStandEditor plugin;
    private FileConfiguration config;

    public ConfigManager(ItzStandEditor plugin) {
        this.plugin = plugin;

        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        if (isCrEnabled()) plugin.getGuiManager().startPlayParticles();
        else plugin.getGuiManager().stopPlayParticles();
    }


    public boolean getTryHookPapi() {
        return config.getBoolean("try_hook_papi", true);
    }

    public boolean getRequireShift() {
        return config.getBoolean("require_shift", true);
    }

    public boolean getSaveSection() {
        return config.getBoolean("save_section", false);
    }

    public boolean getBlockNotArmor() {
        return config.getBoolean("block_not_armor", true);
    }



    public Component getNoPermMessage() {
        return plugin.isHookedPapi() ?
                MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(null, config.getString("translation.noPerm", "<red>У вас нет прав!"))) :
                MiniMessage.miniMessage().deserialize(config.getString("translation.noPerm", "<red>У вас нет прав!"));
    }

    public Component getUsageMessage() {
        return plugin.isHookedPapi() ?
                MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(null, config.getString("translation.usage", "<red>Использование: /itzstandeditor reload"))) :
                MiniMessage.miniMessage().deserialize(config.getString("translation.usage", "<red>Использование: /itzstandeditor reload"));
    }

    public Component getReloadedMessage() {
        return plugin.isHookedPapi() ?
                MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(null, config.getString("translation.reloaded", "<green>ItzStandEditor перезагружен!"))) :
                MiniMessage.miniMessage().deserialize(config.getString("translation.reloaded", "<green>ItzStandEditor перезагружен!"));
    }


    public boolean isCrEnabled() {
        return config.getBoolean("connecting_ray.enabled", true);
    }

    public Particle getCrParticle() {
        return Particle.valueOf(config.getString("connecting_ray.particle", "END_ROD").toUpperCase());
    }

    public int getCrAmount() {
        return config.getInt("connecting_ray.amount", 1);
    }

    public double getCrPeriod() {
        return config.getDouble("connecting_ray.period", 0.5);
    }

    public int getCrRate() {
        return config.getInt("connecting_ray.rate", 10);
    }



    private HashMap<String, ItemStack> getItems(InventoryData data) {
        HashMap<String, ItemStack> items = new HashMap<>();

        for (String itemId : Objects.requireNonNull(config.getConfigurationSection("items")).getKeys(false)) {
            ConfigurationSection configurationSection = config.getConfigurationSection("items." + itemId);

            ItemStack item = new ItemStack(Material.valueOf(Objects.requireNonNull(configurationSection).getString("material", "stone").toUpperCase()));
            ItemMeta itemMeta = item.getItemMeta();

            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.setAttributeModifiers(null);

            itemMeta.displayName(MiniMessage.miniMessage().deserialize("<i:false><white>" +
                    parsePlaceholders(data, configurationSection.getString("name", "?"))));
            itemMeta.lore(convertStringList(configurationSection.getStringList("lore"), data));

            if (configurationSection.getBoolean("enchanted", false)) itemMeta.addEnchant(Enchantment.FORTUNE, 1, true);
            item.setAmount(configurationSection.getInt("amount", 1));

            if (configurationSection.getConfigurationSection("functions") != null) {
                List<String> functions = Lists.newArrayList();
                for (String functionId : Objects.requireNonNull(configurationSection.getConfigurationSection("functions")).getKeys(false)) {
                    String clickType = config.getString("items." + itemId + ".functions." + functionId, "").toUpperCase();
                    if (clickType.equals("LEFT") || clickType.equals("RIGHT") || clickType.equals("ALL")) {
                        functions.add(functionId + "*" + clickType);
                    }
                }
                itemMeta.getPersistentDataContainer().set(plugin.getNskFunctions(), PersistentDataType.LIST.strings(), functions);
            }
            item.setItemMeta(itemMeta);

            items.put(itemId, item);
        }

        return items;
    }

    public ArrayList<String> getSections() {
        return new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("sections")).getKeys(false));
    }

    public Inventory getInventory(InventoryData data) {
        return Bukkit.createInventory(
                data,
                config.getInt("menu.size", 27),
                plugin.isHookedPapi() ?
                        MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(plugin.getGuiManager().getPlayer(data), config.getString("menu.title", " » Редактирование стенда"))) :
                        MiniMessage.miniMessage().deserialize(config.getString("menu.title", " » Редактирование стенда")));
    }

    public void placeItems(InventoryData data) {
        for (String itemId : Objects.requireNonNull(config.getConfigurationSection("menu.items")).getKeys(false)) {
            List<Integer> slots = config.getIntegerList("menu.items." + itemId);
            if (slots.isEmpty()) {
                slots.add(config.getInt("menu.items." + itemId, 0));
            }
            for (int slot : slots) {
                data.getInventory().setItem(slot, getItems(data).get(itemId));
            }
        }

        String sectionId = new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("sections")).getKeys(false)).get(data.getSection());

        for (String itemId : Objects.requireNonNull(config.getConfigurationSection("sections." + sectionId + ".items")).getKeys(false)) {
            List<Integer> slots = config.getIntegerList("sections." + sectionId + ".items." + itemId);
            if (slots.isEmpty()) {
                slots.add(config.getInt("sections." + sectionId + ".items." + itemId, 0));
            }
            for (int slot : slots) {
                data.getInventory().setItem(slot, getItems(data).get(itemId));
            }
        }
    }

    private String getSectionName(int section, boolean isActive) {
        String name;

        if (isActive) {
            name = config.getString("sections_template.active", "<#1f6600>• <#4dff00>%name%");
        }
        else {
            name = config.getString("sections_template.not_active", "<#2a2a2a>• <#6a6a6a>%name%");
        }

        String sectionId = new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("sections")).getKeys(false)).get(section);
        name = name.replaceAll("%name%", config.getString("sections." + sectionId + ".name", "?"));

        return name;
    }

    private String getStatusTranslation(boolean isOn) {
        if (isOn) {
            return config.getString("translation.on", "<green>Включено");
        }
        else {
            return config.getString("translation.off", "<red>Выключено");
        }
    }



    private ArrayList<Component> convertStringList(List<String> strings, InventoryData data) {
        ArrayList<Component> components = new ArrayList<>();

        for (String string : strings) {
            if (string.contains("%sections%")) {
                for (int i = 0; i < getSections().size(); i++) {
                    components.add(MiniMessage.miniMessage().deserialize("<i:false><white>" +
                            parsePlaceholders(data, string.replaceAll("%sections%", getSectionName(i, i == data.getSection())))));
                }
                continue;
            }

            components.add(MiniMessage.miniMessage().deserialize("<i:false><white>" + parsePlaceholders(data, string)));
        }

        return components;
    }

    public String parsePlaceholders(InventoryData data, String string) {
        ArmorStand stand = data.getStand();

        String returnString = string
                .replaceAll("%player%", plugin.getGuiManager().getPlayer(data).getName())

                .replaceAll("%armor_head%", "<lang:" + stand.getItem(EquipmentSlot.HEAD).translationKey() + ">")
                .replaceAll("%armor_chest%", "<lang:" + stand.getItem(EquipmentSlot.CHEST).translationKey() + ">")
                .replaceAll("%armor_legs%", "<lang:" + stand.getItem(EquipmentSlot.LEGS).translationKey() + ">")
                .replaceAll("%armor_feet%", "<lang:" + stand.getItem(EquipmentSlot.FEET).translationKey() + ">")
                .replaceAll("%armor_hand%", "<lang:" + stand.getItem(EquipmentSlot.HAND).translationKey() + ">")
                .replaceAll("%armor_offhand%", "<lang:" + stand.getItem(EquipmentSlot.OFF_HAND).translationKey() + ">")

                .replaceAll("%blocked_head%", getStatusTranslation(stand.isSlotDisabled(EquipmentSlot.HEAD)))
                .replaceAll("%blocked_chest%", getStatusTranslation(stand.isSlotDisabled(EquipmentSlot.CHEST)))
                .replaceAll("%blocked_legs%", getStatusTranslation(stand.isSlotDisabled(EquipmentSlot.LEGS)))
                .replaceAll("%blocked_feet%", getStatusTranslation(stand.isSlotDisabled(EquipmentSlot.FEET)))
                .replaceAll("%blocked_hand%", getStatusTranslation(stand.isSlotDisabled(EquipmentSlot.HAND)))

                .replaceAll("%appear_plate%", getStatusTranslation(stand.hasBasePlate()))
                .replaceAll("%appear_arms%", getStatusTranslation(stand.hasArms()))
                .replaceAll("%appear_invisibility%", getStatusTranslation(stand.isInvisible()))
                .replaceAll("%appear_glowing%", getStatusTranslation(stand.isGlowing()))
                .replaceAll("%appear_small%", getStatusTranslation(stand.isSmall()))
                .replaceAll("%appear_gravity%", getStatusTranslation(stand.hasGravity()))
                .replaceAll("%appear_invulnerability%", getStatusTranslation(stand.isInvulnerable()))

                .replaceAll("%rotate_head_x%", String.valueOf(stand.getHeadRotations().x()))
                .replaceAll("%rotate_head_y%", String.valueOf(stand.getHeadRotations().y()))
                .replaceAll("%rotate_head_z%", String.valueOf(stand.getHeadRotations().z()))

                .replaceAll("%rotate_leftarm_x%", String.valueOf(stand.getLeftArmRotations().x()))
                .replaceAll("%rotate_leftarm_y%", String.valueOf(stand.getLeftArmRotations().y()))
                .replaceAll("%rotate_leftarm_z%", String.valueOf(stand.getLeftArmRotations().z()))

                .replaceAll("%rotate_rightarm_x%", String.valueOf(stand.getRightArmRotations().x()))
                .replaceAll("%rotate_rightarm_y%", String.valueOf(stand.getRightArmRotations().y()))
                .replaceAll("%rotate_rightarm_z%", String.valueOf(stand.getRightArmRotations().z()))

                .replaceAll("%rotate_leftleg_x%", String.valueOf(stand.getLeftLegRotations().x()))
                .replaceAll("%rotate_leftleg_y%", String.valueOf(stand.getLeftLegRotations().y()))
                .replaceAll("%rotate_leftleg_z%", String.valueOf(stand.getLeftLegRotations().z()))

                .replaceAll("%rotate_rightleg_x%", String.valueOf(stand.getRightLegRotations().x()))
                .replaceAll("%rotate_rightleg_y%", String.valueOf(stand.getRightLegRotations().y()))
                .replaceAll("%rotate_rightleg_z%", String.valueOf(stand.getRightLegRotations().z()));

        return plugin.isHookedPapi() ?
                PlaceholderAPI.setPlaceholders(plugin.getGuiManager().getPlayer(data), returnString) :
                returnString;
    }
}