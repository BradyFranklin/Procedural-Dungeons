package org.ninenetwork.infinitedungeons.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AccessoryUtil {

    public static void storeItems(List<ItemStack> items, Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (items.isEmpty()) {
            data.set(new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "Accessory"), PersistentDataType.STRING, "");
        } else {
            try {
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                os.writeInt(items.size());
                for (int i = 0; i < items.size(); i++) {
                    os.writeObject(items.get(i));
                }
                os.flush();
                byte[] rawData = io.toByteArray();
                String encodedData = Base64.getEncoder().encodeToString(rawData);
                data.set(new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "Accessory"), PersistentDataType.STRING, encodedData);
                os.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }

        }
    }

    public static ArrayList<ItemStack> getItems(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        ArrayList<ItemStack> items = new ArrayList<>();
        String encodedItems = data.get(new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "Accessory"), PersistentDataType.STRING);

        if (!encodedItems.isEmpty()) {
            byte[] rawData = Base64.getDecoder().decode(encodedItems);
            try {
                ByteArrayInputStream io = new ByteArrayInputStream(rawData);
                BukkitObjectInputStream in = new BukkitObjectInputStream(io);
                int itemsCount = in.readInt();
                for (int i = 0; i < itemsCount; i++) {
                    items.add((ItemStack) in.readObject());
                }
                in.close();
            } catch (IOException ex) {
                System.out.println(ex);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return items;
    }

}