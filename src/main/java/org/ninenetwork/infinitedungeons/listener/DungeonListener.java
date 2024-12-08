package org.ninenetwork.infinitedungeons.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompParticle;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.classes.DungeonClass;
import org.ninenetwork.infinitedungeons.classes.DungeonClassUtil;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.door.DungeonDoorGeneral;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.dungeon.secret.SecretManager;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSource;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStatSourceDatabase;
import org.ninenetwork.infinitedungeons.playerstats.health.PlayerHealthHandler;
import org.ninenetwork.infinitedungeons.settings.Settings;
import org.ninenetwork.infinitedungeons.util.AccessoryUtil;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.*;

public class DungeonListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        PlayerCache.from(player);
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!data.has(new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "Accessory"), PersistentDataType.STRING)) {
            data.set(new NamespacedKey(InfiniteDungeonsPlugin.getInstance(), "Accessory"), PersistentDataType.STRING, "");
        }
        Map<PlayerStat, Double> baseStats = new HashMap<>();
        baseStats.put(PlayerStat.HEALTH, 100.0);
        baseStats.put(PlayerStat.CRIT_CHANCE, 30.0);
        baseStats.put(PlayerStat.CRIT_DAMAGE, 50.0);
        baseStats.put(PlayerStat.HEALTH_REGEN, 100.0);
        baseStats.put(PlayerStat.VITALITY, 100.0);
        PlayerStatSourceDatabase.getInstance().addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.BASE, baseStats, false);
        PlayerStatManager.setAllPlayerStats(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerCache cache = PlayerCache.from(player);
        cache.save();
        cache.removeFromMemory();
    }

    @EventHandler
    public void onPlayerHeldItem(PlayerItemHeldEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItem(event.getNewSlot());
            ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
            //PlayerStatManager.updateItemStats(player, oldItem, item);
            CustomDungeonItemManager itemManager = CustomDungeonItemManager.getInstance();
            PlayerHealthHandler.updatePlayersHealthPercentage(player);
            if (itemManager.checkIsDungeonItem(item)) {
                AbstractDungeonItem i = itemManager.findDungeonItem(item);
                if (i.getItemType() == ItemType.WEAPON || i.getItemType() == ItemType.ABILITY_WEAPON) {
                    PlayerStatSourceDatabase.getInstance().addFullStatsFromSpecificSourceToMapping(player, PlayerStatSource.WEAPON, itemManager.findDungeonItem(item).itemStatData(), false);
                    PlayerStatManager.setAllPlayerStats(player);
                }
            } else {
                PlayerStatSourceDatabase.getInstance().removeAllStatsFromSource(player, PlayerStatSource.WEAPON);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            Player player = event.getPlayer();
            PlayerCache cache = PlayerCache.from(player);
            if (cache.hasDungeon()) {
                DungeonClass dungeonClass = DungeonClass.findClassByLabel(cache.getCurrentDungeonClass());
                if (dungeonClass == DungeonClass.MAGE) {
                    DungeonClassUtil.performMageUlt(player);
                } else if (dungeonClass == DungeonClass.BERSERK) {
                    DungeonClassUtil.performBerserkUlt(player);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().equalsIgnoreCase("Accessory Bag")) {
            ArrayList<ItemStack> prunedItems = new ArrayList<>();

            Arrays.stream(event.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .forEach(prunedItems::add);

            AccessoryUtil.storeItems(prunedItems, player);

            PlayerStatManager.evaluateTalismanStats(player, prunedItems);
        }
    }

    @EventHandler
    public void onInteractAtArmorStand(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("Accessory Bag")) {
            Common.tell(player, "Please only use shift-click to add/remove talismans");
            event.setCancelled(true);
            /*
            ItemStack item = event.getOldCursor();
            ItemStack i = event.getCursor();
            Common.tell(player, "Old Cursor: " + item.getType());
            Common.tell(player, "Drag Cursor: " + i.getType());
            if (event.getInventory().getType() != InventoryType.PLAYER) {
                if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(item)) {
                    AbstractDungeonItem talisman = CustomDungeonItemManager.getInstance().findDungeonItem(item);
                    if (talisman.getItemType() != ItemType.TALISMAN) {
                        Common.tell(player, "Cancelled from drag");
                        Common.tell(player, "You can only take/deposit talismans in this menu.");
                        event.setCancelled(true);
                    }
                }
            }

             */
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("Accessory Bag")) {
            ItemStack item = event.getCurrentItem();
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                if (item != null) {
                    if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(item)) {
                        AbstractDungeonItem talisman = CustomDungeonItemManager.getInstance().findDungeonItem(item);
                        if (talisman.getItemType() != ItemType.TALISMAN) {
                            Common.tell(player, "You can only take/deposit talismans in this menu.");
                            event.setCancelled(true);
                        }
                        if (item.getAmount() > 1) {
                            Common.tell(player, "You cannot move stacked items into your accessory bag.");
                            event.setCancelled(true);
                        }
                        for (ItemStack i : AccessoryUtil.getItems(player)) {
                            if (item.getItemMeta().getDisplayName().equals(i.getItemMeta().getDisplayName())) {
                                Common.tell(player, "You can only have one of each talisman in your accessory bag.");
                                if (!item.equals(i)) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            } else {
                Common.tell(player, "Please only use shift-click to add/remove talismans");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        MapView view = event.getMap();
        if (view.getWorld() != null) {
            if (view.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                double x = view.getCenterX();
                double z = view.getCenterZ();
                event.getMap().setScale(MapView.Scale.FAR);
                Dungeon dungeon = Dungeon.findByLocation(new Location(view.getWorld(), x, 6, z));
                if (dungeon != null) {
                    Region region = dungeon.getRegion();
                    if (region != null) {
                        Location location = GeneralUtils.findMidPoint(region.getPrimary(), region.getSecondary());
                        view.setCenterX((int) location.getX());
                        view.setCenterZ((int) location.getZ());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerCache cache = PlayerCache.from(player);
        Block block = event.getClickedBlock();
        if (event.getPlayer().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (cache.hasDungeon()) {
                Dungeon dungeon = cache.getCurrentDungeon();
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && block != null) {
                    if (block.getType() == Material.COAL_BLOCK) {
                        DungeonDoorGeneral.runDungeonDoorOpenAttempt(dungeon, cache, block);
                    } else if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
                        Location blockLocation = block.getLocation();
                        DungeonRoomInstance dungeonRoomInstance = DungeonRoomInstance.getRoomFromAnyLocation(dungeon, blockLocation);
                        if (dungeonRoomInstance != null) {
                            boolean runAndCancel = SecretManager.handleSecretClickAttempt(dungeon, player, dungeonRoomInstance, block);
                            event.setCancelled(runAndCancel);
                        }
                    } else if (block.getType() == Material.PLAYER_HEAD) {
                        Location blockLocation = block.getLocation();
                        DungeonRoomInstance dungeonRoomInstance = DungeonRoomInstance.getRoomFromAnyLocation(dungeon, blockLocation);
                        if (dungeonRoomInstance != null) {
                            boolean runAndCancel = SecretManager.handleSecretClickAttempt(dungeon, player, dungeonRoomInstance, block);
                            event.setCancelled(runAndCancel);
                        }
                    }
                } else if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    if (cache.getCurrentDungeonClass().equals("Mage")) {
                        if (player.getEquipment().getItemInMainHand().getType() != Material.BOW) {
                            if (CustomDungeonItemManager.getInstance().checkIsDungeonItem(player.getEquipment().getItemInMainHand())) {
                                AbstractDungeonItem item = CustomDungeonItemManager.getInstance().findDungeonItem(player.getEquipment().getItemInMainHand());
                                if (item.getItemType() == ItemType.ABILITY_WEAPON) {
                                    Vector playerVector = player.getLocation().clone().add(0.0, 1.3, 0.0).toVector();
                                    Vector lookingVector = player.getTargetBlock(null, 10).getLocation().toVector();

                                    Vector betweenVector = lookingVector.clone().subtract(playerVector);
                                    Vector directionVector = betweenVector.clone().normalize();

                                    for (int i = 0; i < betweenVector.length(); i++) {
                                        Vector particlePoint = playerVector.clone().add(directionVector.clone().multiply(i));
                                        CompParticle.ELECTRIC_SPARK.spawn(particlePoint.toLocation(player.getWorld()));

                            /*
                            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                                    particlePoint.getX(),

                                    particlePoint.getY() + 1,
                                    particlePoint.getZ(),
                                    0, 0.001, 1, 0, 1,
                                    new Particle.DustOptions(Color.PURPLE, 1));

                             */
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Enderman) {
            if (entity.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                Location loc = entity.getLocation();
                event.setTo(loc);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
                player.setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        if (event.getFrom().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            PlayerHealthHandler.worldChangeHealthGenerator(event.getPlayer(), 20);
            event.getPlayer().setFoodLevel(20);
        } else if (event.getPlayer().getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            PlayerHealthHandler.worldChangeHealthGenerator(event.getPlayer(), 40);
            event.getPlayer().setFoodLevel(20);
        }
    }

}