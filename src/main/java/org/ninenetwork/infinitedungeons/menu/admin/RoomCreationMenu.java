package org.ninenetwork.infinitedungeons.menu.admin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.prompt.RoomCreationNamePrompt;

public class RoomCreationMenu extends Menu {

    @Position (12)
    private final Button createNewButton;
    //@Position(14)
    //private final Button editRoomButton;

    private final Button fillButton;

    public RoomCreationMenu() {
        this.setTitle("Dungeon Room Manager");
        this.setSize(9 * 3);

        this.createNewButton = new ButtonMenu(new RoomCreationSizeMenu(), ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE, "&e&lCreate New Room"));

        //this.editRoomButton = new ButtonMenu(new RoomEditMenu(), ItemCreator.of(CompMaterial.WHITE_STAINED_GLASS_PANE));

        fillButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {}
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

    @Override
    public ItemStack getItemAt(int slot) {
        return fillButton.getItem();
    }

    private class RoomCreationSizeMenu extends Menu {

        @Position(10)
        private final Button a1x1_Square;
        @Position(11)
        private final Button a1x2_Rectangle;
        @Position(12)
        private final Button a1x3_Rectangle;
        @Position(14)
        private final Button a1x4_Rectangle;
        @Position(15)
        private final Button a1x1x1_L;
        @Position(16)
        private final Button a2x2_Square;

        private final Button fillButton;

        RoomCreationSizeMenu() {
            this.setTitle("Select A Room Size");
            this.setSize(9 * 3);

            this.a1x1_Square = new ButtonMenu(new RoomCreationTypeMenu(), ItemCreator.of(CompMaterial.CRYING_OBSIDIAN, "&e&l1x1 &6(Square)"));

            a1x2_Rectangle = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x2_Rectangle");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&e&l1x2 &6(Rectangle)").make();
                }
            };
            a1x3_Rectangle = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x3_Rectangle");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&e&l1x3 &6(Rectangle)").make();
                }
            };
            a1x4_Rectangle = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x4_Rectangle");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&e&l1x4 &6(Rectangle)").make();
                }
            };
            a2x2_Square = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("2x2_Square");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&e&l2x2 &6(Square)").make();
                }
            };
            a1x1x1_L = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x1x1_L");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&e&l1x1x1 &6(L-Shaped)").make();
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

        @Override
        public ItemStack getItemAt(int slot) {
            return fillButton.getItem();
        }

    }

    private class RoomCreationTypeMenu extends Menu {

        @Position(10)
        private final Button bloodButton;
        @Position(12)
        private final Button lobbyButton;
        @Position(14)
        private final Button bloodRushButton;
        @Position(16)
        private final Button puzzleButton;

        private final Button fillButton;

        RoomCreationTypeMenu() {
            this.setTitle("Select A Room Type");
            this.setSize(9 * 3);

            bloodButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x1_Square");
                    cache.setRoomCreatorTypeIdentifier("Blood");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&eBlood Room").make();
                }
            };
            lobbyButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x1_Square");
                    cache.setRoomCreatorTypeIdentifier("Lobby");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&eLobby Room").make();
                }
            };
            bloodRushButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x1_Square");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&eNormal Room").make();
                }
            };
            puzzleButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    cache.setRoomCreatorSizeIdentifier("1x1_Square");
                    cache.setRoomCreatorTypeIdentifier("Bloodrush");
                    RoomCreationNamePrompt.show(player, new RoomCreationNamePrompt());
                }
                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.PAPER, "&ePuzzle Room").make();
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

        @Override
        public ItemStack getItemAt(int slot) {
            return fillButton.getItem();
        }

    }


}