package org.ninenetwork.infinitedungeons.listener.armorevent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.Common;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;
import org.ninenetwork.infinitedungeons.playerstats.health.PlayerHealthHandler;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Called when a player equips or unequips a piece of armor.
 *
 * @author Arnah
 * @since Jul 30, 2015
 */
public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorType type;
    private ItemStack oldArmorPiece, newArmorPiece;

    /**
     * Registers the listeners for this event. If you forget to call this method, then the event will never get caled.
     * @param plugin Plugin to call this event from
     */
    public static void registerListener(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(new ArmorListener(getBlockedMaterialNames(plugin)), plugin);
        try{
            //Better way to check for this? Only in 1.13.1+?
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");
            Bukkit.getServer().getPluginManager().registerEvents(new DispenserArmorListener(), plugin);
        } catch(Exception ignored) {

        }
    }

    private static List<String> getBlockedMaterialNames(JavaPlugin plugin) {
        try (InputStream inputStream = plugin.getResource("armorequipevent-blocked.txt")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.toList());
            } catch (Exception ignored1) {

            }
        } catch (IOException ignored2) {
            //e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Creates a new ArmorEquipEvent
     * @param player The player who put on / removed the armor.
     * @param type The ArmorType of the armor added
     * @param oldArmorPiece The ItemStack of the armor removed.
     * @param newArmorPiece The ItemStack of the armor added.
     */
    public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece){
        super(player);
        this.equipType = equipType;
        this.type = type;
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
        CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
        Common.tell(player, "Ran Armor Event");
        PlayerHealthHandler.updatePlayersHealthPercentage(player);
        if (itemManager.checkIsDungeonItem(newArmorPiece)) {
            Common.tell(player, "Passed New Armor Is Dungeon Item");
            PlayerStatSourceDatabase.getInstance().addFullStatsFromSpecificSourceToMapping(player, GeneralUtils.armorToStatSource(type), itemManager.findDungeonItem(newArmorPiece).itemStatData(), false);
            PlayerStatManager.setAllPlayerStats(player);
        } else {
            Common.tell(player, "ArmorEquipEvent this should count for dequip?");
            PlayerStatSourceDatabase.getInstance().removeAllStatsFromSource(player, GeneralUtils.armorToStatSource(type));
        }
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    public static HandlerList getHandlerList(){
        return handlers;
    }

    /**
     * Gets a list of handlers handling this event.
     *
     * @return A list of handlers handling this event.
     */
    @Override
    public final HandlerList getHandlers(){
        return handlers;
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled. When the event is cancelled, the armor is not changed.
     */
    public final void setCancelled(final boolean cancel){
        this.cancel = cancel;
    }

    /**
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public final boolean isCancelled(){
        return cancel;
    }

    /**
     * Returns the type of armor involved in this event
     * @return ArmorType
     */
    public final ArmorType getType(){
        return type;
    }

    /**
     * Returns the last equipped armor piece, could be a piece of armor, or null
     */
    public final ItemStack getOldArmorPiece(){
        if(ArmorListener.isEmpty(oldArmorPiece)){
            return null;
        }
        return oldArmorPiece;
    }

    public final void setOldArmorPiece(final ItemStack oldArmorPiece){
        this.oldArmorPiece = oldArmorPiece;
    }

    /**
     * Returns the newly equipped armor or null if the armor was unequipped
     * @return ItemStack of armor or null
     */
    @Nullable
    public final ItemStack getNewArmorPiece(){
        if(ArmorListener.isEmpty(newArmorPiece)){
            return null;
        }
        return newArmorPiece;
    }

    /**
     * Sets the new armor piece
     * @param newArmorPiece The new armor piece
     */
    public final void setNewArmorPiece(@Nullable final ItemStack newArmorPiece){
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets the method used to either equip or unequip an armor piece.
     */
    public EquipMethod getMethod(){
        return equipType;
    }

    /**
     * Represents the way of equipping or uneqipping armor.
     */
    public enum EquipMethod{// These have got to be the worst documentations ever.
        /**
         * When you shift click an armor piece to equip or unequip
         */
        SHIFT_CLICK,
        /**
         * When you drag and drop the item to equip or unequip
         */
        DRAG,
        /**
         * When you manually equip or unequip the item. Use to be DRAG
         */
        PICK_DROP,
        /**
         * When you right click an armor piece in the hotbar without the inventory open to equip.
         */
        HOTBAR,
        /**
         * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
         */
        HOTBAR_SWAP,
        /**
         * When in range of a dispenser that shoots an armor piece to equip.<br>
         * Requires the spigot version to have {@link org.bukkit.event.block.BlockDispenseArmorEvent} implemented.
         */
        DISPENSER,
        /**
         * When an armor piece is removed due to it losing all durability.
         */
        BROKE,
        /**
         * When you die causing all armor to unequip
         */
        DEATH,
    }
}