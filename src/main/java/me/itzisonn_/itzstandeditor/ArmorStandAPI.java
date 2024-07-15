package me.itzisonn_.itzstandeditor;

import com.google.common.collect.Lists;
import io.papermc.paper.math.Rotations;
import me.itzisonn_.itzstandeditor.enums.AppearanceType;
import me.itzisonn_.itzstandeditor.enums.CopyType;
import me.itzisonn_.itzstandeditor.enums.RotationAxis;
import me.itzisonn_.itzstandeditor.enums.RotationType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ArmorStandAPI {

    public ItemStack changeArmor(ArmorStand stand, EquipmentSlot slot, ItemStack item) {
        ItemStack itemOnStand = stand.getItem(slot);
        stand.setItem(slot, item);

        return itemOnStand;
    }

    public void toggleBlockArmorSlot(ArmorStand stand, EquipmentSlot slot) {
        if (stand.isSlotDisabled(slot)) {
            stand.removeDisabledSlots(slot);
        }
        else {
            stand.addDisabledSlots(slot);
        }
    }

    public void changeRotation(ArmorStand stand, boolean isNegative, boolean isExtended, RotationType type, RotationAxis axis) {
        double x = 0;
        double y = 0;
        double z = 0;
        double defx = 0;
        double defy = 0;
        double defz = 0;

        switch (type) {
            case HEAD -> {
                x = stand.getHeadRotations().x();
                y = stand.getHeadRotations().y();
                z = stand.getHeadRotations().z();
                defx = 0;
                defy = 0;
                defz = 0;
            }
            case LEFTARM -> {
                x = stand.getLeftArmRotations().x();
                y = stand.getLeftArmRotations().y();
                z = stand.getLeftArmRotations().z();
                defx = -10;
                defy = 0;
                defz = -10;
            }
            case RIGHTARM -> {
                x = stand.getRightArmRotations().x();
                y = stand.getRightArmRotations().y();
                z = stand.getRightArmRotations().z();
                defx = -15;
                defy = 0;
                defz = 10;
            }
            case LEFTLEG -> {
                x = stand.getLeftLegRotations().x();
                y = stand.getLeftLegRotations().y();
                z = stand.getLeftLegRotations().z();
                defx = -1;
                defy = 0;
                defz = -1;
            }
            case RIGHTLEG -> {
                x = stand.getRightLegRotations().x();
                y = stand.getRightLegRotations().y();
                z = stand.getRightLegRotations().z();
                defx = 1;
                defy = 0;
                defz = 1;
            }
        }

        int change = 1;
        if (isNegative) change = -1;
        if (isExtended) change *= 10;

        switch (axis) {
            case X -> x += change;
            case Y -> y += change;
            case Z -> z += change;
            case CLEAR -> {
                x = defx;
                y = defy;
                z = defz;
            }
        }

        switch (type) {
            case HEAD -> stand.setHeadRotations(Rotations.ofDegrees(x, y, z));
            case LEFTARM -> stand.setLeftArmRotations(Rotations.ofDegrees(x, y, z));
            case RIGHTARM -> stand.setRightArmRotations(Rotations.ofDegrees(x, y, z));
            case LEFTLEG -> stand.setLeftLegRotations(Rotations.ofDegrees(x, y, z));
            case RIGHTLEG -> stand.setRightLegRotations(Rotations.ofDegrees(x, y, z));
        }
    }

    public void changeAppearance(ArmorStand stand, AppearanceType type) {
        switch (type) {
            case PLATE -> stand.setBasePlate(!stand.hasBasePlate());
            case ARMS -> stand.setArms(!stand.hasArms());
            case INVISIBILITY -> stand.setInvisible(!stand.isInvisible());
            case GLOWING -> stand.setGlowing(!stand.isGlowing());
            case SMALL -> stand.setSmall(!stand.isSmall());
            case GRAVITY -> stand.setGravity(!stand.hasGravity());
            case INVULNERABILITY -> stand.setInvulnerable(!stand.isInvulnerable());
        }
    }

    public ArrayList<Object> copyData(ArmorStand stand, CopyType type) {
        return switch (type) {
            case ROTATE_HEAD -> Lists.newArrayList(stand.getHeadRotations().x(), stand.getHeadRotations().y(), stand.getHeadRotations().z());
            case ROTATE_LEFTARM -> Lists.newArrayList(stand.getLeftArmRotations().x(), stand.getLeftArmRotations().y(), stand.getLeftArmRotations().z());
            case ROTATE_RIGHTARM -> Lists.newArrayList(stand.getRightArmRotations().x(), stand.getRightArmRotations().y(), stand.getRightArmRotations().z());
            case ROTATE_LEFTLEG -> Lists.newArrayList(stand.getLeftLegRotations().x(), stand.getLeftLegRotations().y(), stand.getLeftLegRotations().z());
            case ROTATE_RIGHTLEG -> Lists.newArrayList(stand.getRightLegRotations().x(), stand.getRightLegRotations().y(), stand.getRightLegRotations().z());

            case APPEAR_PLATE -> Lists.newArrayList(stand.hasBasePlate());
            case APPEAR_ARMS -> Lists.newArrayList(stand.hasArms());
            case APPEAR_INVISIBILITY -> Lists.newArrayList(stand.isInvisible());
            case APPEAR_GLOWING -> Lists.newArrayList(stand.isGlowing());
            case APPEAR_SMALL -> Lists.newArrayList(stand.isSmall());
            case APPEAR_GRAVITY -> Lists.newArrayList(stand.hasGravity());
            case APPEAR_INVULNERABILITY -> Lists.newArrayList(stand.isInvulnerable());

            case ARMOR_HEAD -> Lists.newArrayList(stand.getItem(EquipmentSlot.HEAD));
            case ARMOR_CHEST -> Lists.newArrayList(stand.getItem(EquipmentSlot.CHEST));
            case ARMOR_LEGS -> Lists.newArrayList(stand.getItem(EquipmentSlot.LEGS));
            case ARMOR_FEET -> Lists.newArrayList(stand.getItem(EquipmentSlot.FEET));
            case ARMOR_HAND -> Lists.newArrayList(stand.getItem(EquipmentSlot.HAND));
            case ARMOR_OFFHAND -> Lists.newArrayList(stand.getItem(EquipmentSlot.OFF_HAND));
        };
    }

    public void pasteData(ArmorStand stand, CopyType type, ArrayList<Object> data) {
        if (data == null) return;

        switch (type) {
            case ROTATE_HEAD -> stand.setHeadRotations(Rotations.ofDegrees((double) data.get(0), (double) data.get(1), (double) data.get(2)));
            case ROTATE_LEFTARM -> stand.setLeftArmRotations(Rotations.ofDegrees((double) data.get(0), (double) data.get(1), (double) data.get(2)));
            case ROTATE_RIGHTARM -> stand.setRightArmRotations(Rotations.ofDegrees((double) data.get(0), (double) data.get(1), (double) data.get(2)));
            case ROTATE_LEFTLEG -> stand.setLeftLegRotations(Rotations.ofDegrees((double) data.get(0), (double) data.get(1), (double) data.get(2)));
            case ROTATE_RIGHTLEG -> stand.setRightLegRotations(Rotations.ofDegrees((double) data.get(0), (double) data.get(1), (double) data.get(2)));

            case APPEAR_PLATE -> stand.setBasePlate((boolean) data.get(0));
            case APPEAR_ARMS -> stand.setArms((boolean) data.get(0));
            case APPEAR_INVISIBILITY -> stand.setInvisible((boolean) data.get(0));
            case APPEAR_GLOWING -> stand.setGlowing((boolean) data.get(0));
            case APPEAR_SMALL -> stand.setSmall((boolean) data.get(0));
            case APPEAR_GRAVITY -> stand.setGravity((boolean) data.get(0));
            case APPEAR_INVULNERABILITY -> stand.setInvulnerable((boolean) data.get(0));

            case ARMOR_HEAD -> stand.setItem(EquipmentSlot.HEAD, (ItemStack) data.get(0));
            case ARMOR_CHEST -> stand.setItem(EquipmentSlot.CHEST, (ItemStack) data.get(0));
            case ARMOR_LEGS -> stand.setItem(EquipmentSlot.LEGS, (ItemStack) data.get(0));
            case ARMOR_FEET -> stand.setItem(EquipmentSlot.FEET, (ItemStack) data.get(0));
            case ARMOR_HAND -> stand.setItem(EquipmentSlot.HAND, (ItemStack) data.get(0));
            case ARMOR_OFFHAND -> stand.setItem(EquipmentSlot.OFF_HAND, (ItemStack) data.get(0));
        }
    }
}
