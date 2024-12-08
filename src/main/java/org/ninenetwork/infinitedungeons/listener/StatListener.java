package org.ninenetwork.infinitedungeons.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.listener.armorevent.ArmorEquipEvent;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.settings.Settings;

public class StatListener implements Listener {

    @EventHandler
    public void onArmorChange(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        /*
        if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            PlayerCache cache = PlayerCache.from(player);
            // needs to only check second condition if first one is false, workaround for issue with armor event
            if (event.getNewArmorPiece() != null) {
                if (event.getNewArmorPiece().getType() != Material.PLAYER_HEAD) {
                    Common.runLater(20, new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerHealthHandler.runArmorEventChange(player);
                        }
                    });
                } else if (event.getMethod() != ArmorEquipEvent.EquipMethod.HOTBAR) {
                    Common.runLater(20, new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerHealthHandler.runArmorEventChange(player);
                        }
                    });
                }
            } else {
                PlayerHealthHandler.runArmorEventChange(player);
            }
        }
        */
    }

    @EventHandler
    public void onArmorUnequip(InventoryClickEvent event) {
        if (event.getWhoClicked().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                PlayerCache cache = PlayerCache.from((Player) event.getWhoClicked());
                ItemStack item = event.getCurrentItem();
                Common.tell(event.getWhoClicked(), "Item Type: " + item.getType() + ", Slot: " + event.getSlot());
                PlayerStatSource source = getPlayerStatSource(event);
                if (!(item == null || item.getType() == Material.AIR)) {
                    Common.log("Armor Unequip Fired");
                    if (source != PlayerStatSource.FAILSAFE) {
                        //PlayerStatSourceDatabase.getInstance().removeAllStatsFromSource((Player) event.getWhoClicked(), source);
                    }
                }
                /*
                Common.runLater(20, new BukkitRunnable() {
                    @Override
                    public void run() {
                        PlayerHealthHandler.runArmorEventChange((Player) event.getWhoClicked());
                    }
                });
                */
            }
        }

    }

    private static @NotNull PlayerStatSource getPlayerStatSource(InventoryClickEvent event) {
        PlayerStatSource source = PlayerStatSource.FAILSAFE;
        if (event.getSlot() == 5) {
            Common.tell(event.getWhoClicked(), "5 Slot Passed");
            source = PlayerStatSource.ARMOR_HELMET;
        } else if (event.getSlot() == 6) {
            Common.tell(event.getWhoClicked(), "6 Slot Passed");
            source = PlayerStatSource.ARMOR_CHESTPLATE;
        } else if (event.getSlot() == 7) {
            Common.tell(event.getWhoClicked(), "7 Slot Passed");
            source = PlayerStatSource.ARMOR_LEGGINGS;
        } else if (event.getSlot() == 8) {
            Common.tell(event.getWhoClicked(), "8 Slot Passed");
            source = PlayerStatSource.ARMOR_BOOTS;
        }
        return source;
    }

    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                event.setCancelled(true);
            }
        }
    }

}