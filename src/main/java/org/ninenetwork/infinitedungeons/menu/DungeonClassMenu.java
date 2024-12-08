package org.ninenetwork.infinitedungeons.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.classes.DungeonClassUtil;

import java.util.ArrayList;

public class DungeonClassMenu extends Menu {

    private final Button healerButton;
    private final Button mageButton;
    private final Button berserkButton;
    private final Button archerButton;
    private final Button tankButton;
    private final Button backButton;
    private final Button closeButton;
    private final Button fillButton;

    public DungeonClassMenu() {

        setTitle("Dungeon Classes");
        setSize(9 * 4);

        healerButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setPlayerDungeonClass(player, "Healer");
                Common.tell(player, "Selected Healer Class");
            }

            @Override
            public ItemStack getItem() {
                return createClassButtonItem("Healer",getViewer());
            }
        };

        mageButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setPlayerDungeonClass(player, "Mage");
                Common.tell(player, "Selected Mage Class");
            }

            @Override
            public ItemStack getItem() {
                return createClassButtonItem("Mage",getViewer());
            }
        };

        berserkButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setPlayerDungeonClass(player, "Berserk");
                Common.tell(player, "Selected Berserk Class");
            }

            @Override
            public ItemStack getItem() {
                return createClassButtonItem("Berserk",getViewer());
            }
        };

        archerButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setPlayerDungeonClass(player, "Archer");
                Common.tell(player, "Selected Archer Class");
            }

            @Override
            public ItemStack getItem() {
                return createClassButtonItem("Archer",getViewer());
            }
        };

        tankButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                setPlayerDungeonClass(player, "Tank");
                Common.tell(player, "Selected Tank Class");
            }

            @Override
            public ItemStack getItem() {
                return createClassButtonItem("Tank",getViewer());
            }
        };

        backButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new DungeonMainMenu(player).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.ARROW, "&aGo Back").make();
            }
        };

        closeButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                player.closeInventory();
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.BARRIER, "&cClose").make();
            }
        };

        fillButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
            }
            @Override
            public ItemStack getItem() {
                ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(" ");
                item.setItemMeta(meta);
                return item;
            }
        };

    }

    private static ItemStack createClassButtonItem(String className, Player player) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        if (className.equalsIgnoreCase("Healer")) {
            item.setType(Material.POTION);
        } else if (className.equalsIgnoreCase("Mage")) {
            item.setType(Material.BLAZE_ROD);
        } else if (className.equalsIgnoreCase("Berserk")) {
            item.setType(Material.IRON_SWORD);
        } else if (className.equalsIgnoreCase("Archer")) {
            item.setType(Material.BOW);
        } else if (className.equalsIgnoreCase("Tank")) {
            item.setType(Material.LEATHER_CHESTPLATE);
        }
        ItemMeta meta = item.getItemMeta();
        PlayerCache cache = PlayerCache.from(player);
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7[Lvl " + getClassNameLevel(className,player) + "] " + "&a" + className));
        meta.setLore(generateLore(player, className, cache.getCurrentDungeonClass()));
        item.setItemMeta(meta);
        return item;
    }

    private static void setPlayerDungeonClass(Player player, String className) {
        PlayerCache cache = PlayerCache.from(player);
        cache.setCurrentDungeonClass(className);
    }

    private static int getClassNameLevel(String className, Player player) {
        PlayerCache cache = PlayerCache.from(player);
        if (className.equalsIgnoreCase("Healer")) {
            return cache.getHealerLevel();
        } else if (className.equalsIgnoreCase("Mage")) {
            return cache.getMageLevel();
        } else if (className.equalsIgnoreCase("Berserk")) {
            return cache.getBerserkLevel();
        } else if (className.equalsIgnoreCase("Archer")) {
            return cache.getArcherLevel();
        } else if (className.equalsIgnoreCase("Tank")) {
            return cache.getTankLevel();
        }
        return 0;
    }

    //unfinished
    private static ArrayList<String> generateLore(Player player, String className, String selected) {
        ArrayList<String> lore = new ArrayList<>();
        if (className.equalsIgnoreCase("Healer")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&f&lClass Passives"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aRenew"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aHealing Aura"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aRevive"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aOrbies"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aSoul Tether"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &aOverheal"));
            lore.add(" ");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&f&lDungeon Orb Abilities"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &6Healing Circle"));
            lore.add(" ");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&f&lGhost Abilities"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &fHealing Potion"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8&l* &fRevive Self"));
            lore.add(" ");
        } else if (className.equalsIgnoreCase("Mage")) {

        } else if (className.equalsIgnoreCase("Berserk")) {
            lore.add(Common.colorize("&7Melee Damage: &c+" + (DungeonClassUtil.getBerserkMeleeBoost(player) * 100) + "%"));
            lore.add(" ");
            lore.add(Common.colorize("&f&lClass Passives"));
            lore.add(Common.colorize("&8✦ &aBlood Lust"));
            lore.add(Common.colorize("&8✦ &aLust For Blood"));
            lore.add(Common.colorize("&8✦ &aIndomitable"));
            lore.add(" ");
            lore.add(Common.colorize("&f&lDungeon Orb Ability"));
            lore.add(Common.colorize("&8✦ &6Ragnarok"));

//Your next hit after killing a monster deals 20-40% increased damage. This bonus will expire after 5 seconds. Reduces the cooldown remaining on Throwing Axe by 1 second on activation. Heals you for 3% of your missing SkyBlock icons health.pngHealth every melee hit.
        } else if (className.equalsIgnoreCase("Archer")) {

        } else if (className.equalsIgnoreCase("Tank")) {

        }
        return lore;
    }

    @Override
    public ItemStack getItemAt(final int slot) {
        if (slot == 11) {
            return healerButton.getItem();
        } else if (slot == 12) {
            return mageButton.getItem();
        } else if (slot == 13) {
            return berserkButton.getItem();
        } else if (slot == 14) {
            return archerButton.getItem();
        } else if (slot == 15) {
            return tankButton.getItem();
        } else if (slot == 30) {
            return backButton.getItem();
        } else if (slot == 31) {
            return closeButton.getItem();
        }
        return fillButton.getItem();
    }

    @Override
    protected String[] getInfo() {
        return null;
    }

}