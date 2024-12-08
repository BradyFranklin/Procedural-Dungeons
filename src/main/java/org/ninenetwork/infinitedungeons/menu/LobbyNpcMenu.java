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
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.item.tool.EssenceSecretTool;

import java.util.ArrayList;

public class LobbyNpcMenu extends Menu {

    private final Button playerOneButton;
    private final Button playerOneReadyButton;
    /*
    private final Button playerTwoButton;
    private final Button playerThreeButton;
    private final Button playerFourButton;
    private final Button playerFiveButton;
    private final Button playerTwoReadyButton;
    private final Button playerThreeReadyButton;
    private final Button playerFourReadyButton;
    private final Button playerFiveReadyButton;
    */
    private final Button backButton;
    private final Button closeButton;
    private final Button fillButton;

    public LobbyNpcMenu(Dungeon dungeon, Player player) {

        setTitle("Ready Up");
        setSize(9 * 4);
        PlayerCache cache = PlayerCache.from(player);

        playerOneButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

            }

            @Override
            public ItemStack getItem() {
                return SkullCreator.itemFromName(getViewer().getName());
            }
        };

        playerOneReadyButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

            }

            @Override
            public ItemStack getItem() {
                return generateReadyButton(player, cache.isReady());
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

    private ItemStack generateReadyButton(Player player, boolean isReady) {
        if (isReady) {
            return ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE, "&a&lReady", "&7You are marked as ready",
                    "&7dungeon will start soon").make();
        } else {
            return ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE, "&c&lNot Ready", "&7Click to ready",
                    "&7up to begin the dungeon").make();
        }
    }

    @Override
    public ItemStack getItemAt(final int slot) {
        if (slot == 2) {
            return playerOneButton.getItem();
        } else if (slot == 11) {
            return playerOneReadyButton.getItem();
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