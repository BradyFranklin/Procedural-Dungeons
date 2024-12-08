package org.ninenetwork.infinitedungeons.menu;

import com.gmail.nossr50.datatypes.party.Party;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.button.annotation.Position;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.party.DungeonParty;
import org.ninenetwork.infinitedungeons.prompt.PartyMessagePrompt;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;
import org.ninenetwork.infinitedungeons.util.SkullCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DungeonMainMenu extends Menu {

    @Position(45)
    private final Button dungeonClassButton;

    @Position(48)
    private final Button findPartyButton;

    @Position(49)
    private final Button closeButton;

    @Position(50)
    private final Button autoReadyButton;

    @Position(40)
    private final Button changeTypeButton;

    @Position(12)
    private final Button floorOneButton;

    @Position(11)
    private final Button entranceButton;

    @Position(13)
    private final Button floorTwoButton;

    @Position(14)
    private final Button floorThreeButton;

    @Position(15)
    private final Button floorFourButton;

    @Position(21)
    private final Button floorFiveButton;

    @Position(22)
    private final Button floorSixButton;

    @Position(23)
    private final Button floorSevenButton;

    private final Button fillButton;

    public DungeonMainMenu(Player player) {
        this.setTitle("Dungeons");
        this.setSize(9 * 6);

        this.findPartyButton = new ButtonMenu(new ListedPartyMenu(player, 7), CompMaterial.REDSTONE_BLOCK,
                "&aFind A Party");

        entranceButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 0);
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eEntrance"));
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(" ");
                    item.setItemMeta(meta);
                    return item;
                }
            }
        };

        floorOneButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor I"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 1);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor I"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 1);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorTwoButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor II"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 2);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor II"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 2);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorThreeButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor III"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 3);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor III"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 3);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorFourButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor IV"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 4);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor IV"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 4);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorFiveButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor V"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 5);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor V"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 5);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorSixButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor VI"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 6);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor VI"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 6);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        floorSevenButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    Common.tell(player, "cata");
                } else {
                    Common.tell(player, "master");
                }
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String currentlyViewing = cache.getCurrentViewing();
                if (currentlyViewing.equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ccda0476230e64a917ec1cf26df9b696e2716efe96ae34b9669a6ec525baaa");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs &8- &eFloor VII"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 7);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/ac578027371b3bb636dee2d77d46a0126dfc3d3d40a929d5d4bef06069469a08");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lMM &cCatacombs &8- &eFloor VII"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore = floorLoreBuilder(lore, player, currentlyViewing, 7);
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
            }
        };

        changeTypeButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                Common.tell(player, "clicked");
                PlayerCache cache = PlayerCache.from(player);
                if (cache.getCurrentViewing().equals("Catacombs")) {
                    cache.setCurrentViewing("Master Mode");
                    Common.tell(player, "Debug");
                } else {
                    cache.setCurrentViewing("Catacombs");
                    Common.tell(player, "Debug2");
                }
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                if (cache.getCurrentViewing().equals("Catacombs")) {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/9b5871c72987266e15f1be49b1ec334ef6b618e9653fb78e918abd39563dbb93");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    assert skullMeta != null;
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aThe Catacombs"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8Normal Dungeons Experience"));
                    lore.add(" ");
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to change"));
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                } else {
                    ItemStack item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/e430b897dff6e2ce9ae9d1d95da2d2058ec999a7d4a242afbf156fcadbcc1c89");
                    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                    assert skullMeta != null;
                    skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cMaster Mode"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8Like normal dungeons... but"));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&8more hardcore"));
                    lore.add(" ");
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to change"));
                    skullMeta.setLore(lore);
                    item.setItemMeta(skullMeta);
                    return item;
                }
                //return ItemCreator.of(CompMaterial.RED_STAINED_GLASS, "Debug", "Example").make();
            }
        };

        autoReadyButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                PlayerCache cache = PlayerCache.from(player);
                cache.setAutoReady(!cache.isAutoReady());
                restartMenu();
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                String status;
                CompMaterial material;
                if (cache.isAutoReady()) {
                    status = "&aEnabled";
                    material = CompMaterial.LIME_STAINED_GLASS;
                } else {
                    status = "&cDisabled";
                    material = CompMaterial.RED_STAINED_GLASS;
                }
                return ItemCreator.of(material,
                        "&aToggle Auto Ready Up",
                        "&7Auto ready up when joining &aThe",
                        "&aCatacombs &7if you have the",
                        "&7requirements, so you dont have to",
                        " ",
                        "&aCurrently: " + status,
                        " ",
                        "&eClick to toggle!").make();
            }
        };

        dungeonClassButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new DungeonClassMenu().displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                Player player = getViewer();
                PlayerCache cache = PlayerCache.from(player);
                return ItemCreator.of(CompMaterial.NETHER_STAR,
                        "&aDungeon Classes",
                        "&7View and Select a Dungeon Class",
                        " ",
                        "&aCurrently Selected:&b " + cache.getDungeonClass(),
                        " ",
                        "&eClick to open!").make();
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

    @Override
    public ItemStack getItemAt(int slot) {
        return fillButton.getItem();
    }

    private class StartQueueMenu extends Menu {

        @Position(11)
        private final Button setTypeButton;

        @Position(12)
        private final Button setFloorButton;

        @Position(13)
        private final Button setGroupNoteButton;

        @Position(14)
        private final Button setClassLevelButton;

        @Position(15)
        private final Button setDungeonLevelButton;

        @Position(32)
        private final Button confirmGroupButton;

        @Position(31)
        private final Button backButton;

        private final Button fillButton;

        public StartQueueMenu(Player player) {
            this.setTitle("Group Builder");
            this.setSize(9*4);

            setTypeButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    if (!cache.isInParty()) {
                        if (cache.getCurrentViewing().equalsIgnoreCase("Catacombs")) {
                            cache.setCurrentViewing("Master Mode");
                            restartMenu();
                        } else if (cache.getCurrentViewing().equalsIgnoreCase("Master mode")) {
                            cache.setCurrentViewing("Catacombs");
                            restartMenu();
                        }
                    } else {
                        Common.tell(player, "You cannot use this while in a party");
                    }
                }

                @Override
                public ItemStack getItem() {
                    Player player = getViewer();
                    PlayerCache cache = PlayerCache.from(player);
                    ItemStack item;
                    if (cache.getCurrentViewing().equals("Catacombs")) {
                        item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/9b5871c72987266e15f1be49b1ec334ef6b618e9653fb78e918abd39563dbb93");
                        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                        assert skullMeta != null;
                        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSelect dungeon type"));
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Allows you to change your selected"));
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&7dungeon type."));
                        lore.add(" ");
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&aCurrently selected: &bThe Catacombs"));
                        lore.add(" ");
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to change!"));
                        skullMeta.setLore(lore);
                        item.setItemMeta(skullMeta);
                    } else {
                        item = SkullCreator.createSkullItem("http://textures.minecraft.net/texture/e430b897dff6e2ce9ae9d1d95da2d2058ec999a7d4a242afbf156fcadbcc1c89");
                        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                        assert skullMeta != null;
                        skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aSelect dungeon type"));
                        ArrayList<String> lore = new ArrayList<>();
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Allows you to change your selected"));
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&7dungeon type."));
                        lore.add(" ");
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&aCurrently selected: &bMaster Mode"));
                        lore.add(" ");
                        lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to change!"));
                        skullMeta.setLore(lore);
                        item.setItemMeta(skullMeta);
                    }
                    return item;
                }
            };

            setFloorButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                }

                @Override
                public ItemStack getItem() {
                    PlayerCache cache = PlayerCache.from(getViewer());
                    return ItemCreator.of(CompMaterial.CREEPER_HEAD, "&aSelect floor",
                            "&7Allows you to change your",
                            "&7Selected floor number",
                            " ",
                            "&aCurrently selected: &bFloor " + GeneralUtils.toRomanNumerals(cache.getSelectedFloor())).make();
                }
            };

            setGroupNoteButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    player.closeInventory();
                    PartyMessagePrompt prompt = new PartyMessagePrompt();
                    prompt.show(player);
                }

                @Override
                public ItemStack getItem() {
                    PlayerCache cache = PlayerCache.from(getViewer());
                    return ItemCreator.of(CompMaterial.PAPER,
                            "&aSet Group Note",
                            "&7Set a note to let everyone know",
                            "&7what your gruop plans to do!",
                            " ",
                            "&aCurrent Note: ",
                            "&f" + cache.getGroupNote(),
                            " ",
                            "&eClick to change!").make();
                }
            };

            setClassLevelButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                }

                @Override
                public ItemStack getItem() {
                    PlayerCache cache = PlayerCache.from(getViewer());
                    return ItemCreator.of(CompMaterial.NETHER_STAR, "&aSet Class Level Required",
                            "&7Add a class level requirement to",
                            "&7to the group so only players that have",
                            "&7the desired class level will be",
                            "&7able to join.",
                            " ",
                            "&aCurrent Level Required:",
                            "&f" + cache.getClassLevelRequired(),
                            " ",
                            "&eClick to change!").make();
                }
            };

            setDungeonLevelButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {

                }

                @Override
                public ItemStack getItem() {
                    PlayerCache cache = PlayerCache.from(getViewer());
                    return ItemCreator.of(CompMaterial.WITHER_SKELETON_SKULL, "&aSet Dungeon Level Required",
                            "&7Add a dungeon level requirement to",
                            "&7to the group so only players that have",
                            "&7the desired dungeon level will be",
                            "&7able to join.",
                            " ",
                            "&aCurrent Level Required:",
                            "&f" + cache.getDungeonLevelRequired(),
                            " ",
                            "&eClick to change!").make();
                }
            };

            confirmGroupButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    PlayerCache cache = PlayerCache.from(player);
                    if (!cache.isInParty()) {
                        DungeonParty party = DungeonParty.loadOrCreateDungeonParty(player.getName(), cache.getCurrentViewing(), cache.getSelectedFloor(), cache.getDungeonLevelRequired());
                        party.setCurrentMembers(new ArrayList<>(Arrays.asList(player.getName())));
                        cache.setInParty(true);
                        player.closeInventory();
                    } else {
                        player.closeInventory();
                        Common.tell(player, "You can not do this while already in a party.");
                    }
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EMERALD_BLOCK, "&aConfirm Group",
                            "&7Open up your group so other players",
                            "&7can start joining.",
                            " ",
                            "&eClick to confirm!").make();
                }
            };

            backButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new ListedPartyMenu(player, 1).displayTo(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.ARROW, "&aGo Back",
                            "&7Return to party finder").make();
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

    private class ListedPartyMenu extends MenuPagged<DungeonParty> {

        private final Button fillButton;

        @Position(49)
        private final Button close;

        @Position(46)
        private final Button refreshButton;

        @Position(45)
        private final Button startNewQueue;

        ListedPartyMenu(Player player, int floor) {
            super(9 * 6, DungeonMainMenu.this, DungeonParty.getDungeonPartysByFloor(floor));
            this.setTitle("Party Finder");
            this.setSize(9 * 6);

            startNewQueue = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    new StartQueueMenu(player).displayTo(player);
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.REDSTONE_BLOCK, "&aStart a new queue",
                            "&7Queue up with other players",
                            "&7to defeat a dungeon",
                            " ",
                            "&eClick to queue!").make();
                }
            };

            refreshButton = new Button() {
                @Override
                public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                    restartMenu();
                }

                @Override
                public ItemStack getItem() {
                    return ItemCreator.of(CompMaterial.EMERALD_BLOCK, "&aRefresh",
                            "&7Refresh the list of active",
                            "&7partys matching your search",
                            " ",
                            "&eClick to refresh!").make();

                }
            };

            close = new Button() {
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

        @Override
        protected ItemStack convertToItemStack(DungeonParty dungeonParty) {
            String leaderName = dungeonParty.getLeaderName();
            assert leaderName != null;
            ItemStack item = SkullCreator.createSkullItemFromPlayer(leaderName);
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            assert skullMeta != null;
            skullMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + leaderName + "'s Party"));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon: &bThe Catacombs"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Floor: &b" + dungeonParty.getFloor()));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Level Required: &b" + dungeonParty.getMinimum()));
            lore.add(" ");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Members"));
            assert dungeonParty.getCurrentMembers() != null;
            int count = 0;
            for (String member : dungeonParty.getCurrentMembers()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&f" + member));
                count++;
            }
            for (int i = count; i < 5; i++) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&8Empty"));
            }
            lore.add(" ");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to join"));
            skullMeta.setLore(lore);
            item.setItemMeta(skullMeta);
            return item;
        }

        @Override
        protected void onPageClick(Player player, DungeonParty dungeonParty, ClickType clickType) {
            Common.tell(player, "Registering Click to add to " + dungeonParty.getLeaderName());
            DungeonParty.addPlayerToParty(dungeonParty.getLeaderName(), player);
        }

        @Override
        public Button formNextButton() {
            return super.formNextButton();
        }

        @Override
        protected int getNextButtonPosition() {
            return 26;
        }

        @Override
        protected int getPreviousButtonPosition() {
            return 18;
        }

        @Override
        protected int getReturnButtonPosition() {
            return 48;
        }

        @Override
        protected List<Button> getButtonsToAutoRegister() {
            return super.getButtonsToAutoRegister();
        }
    }

    private static int getRequiredLevel(int floor, String viewType) {
        int requiredLevel = 0;
        if (floor == 0) {
            requiredLevel = 0;
        } else if (floor == 1) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 1;
            } else {
                requiredLevel = 24;
            }
        } else if (floor == 2) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 3;
            } else {
                requiredLevel = 26;
            }
        } else if (floor == 3) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 5;
            } else {
                requiredLevel = 28;
            }
        } else if (floor == 4) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 9;
            } else {
                requiredLevel = 30;
            }
        } else if (floor == 5) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 14;
            } else {
                requiredLevel = 32;
            }
        } else if (floor == 6) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 19;
            } else {
                requiredLevel = 34;
            }
        } else if (floor == 7) {
            if (viewType.equals("Catacombs")) {
                requiredLevel = 24;
            } else {
                requiredLevel = 36;
            }
        }
        return requiredLevel;
    }

    private static ArrayList<String> floorLoreBuilder(ArrayList<String> lore, Player player, String viewType, int floor) {
        boolean unlocked = false;
        PlayerCache cache = PlayerCache.from(player);
        int requiredLevel = getRequiredLevel(floor, viewType);
        String add;
        if (viewType.equals("Master Mode")) {
            add = ChatColor.translateAlternateColorCodes('&', "&c&lMASTER ");
        } else {
            add = "";
        }
        if (cache.getDungeonLevel() >= requiredLevel) {
            unlocked = true;
        }
        if (floor == 0) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bTiny"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Mini-Boss &cThe Watcher &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Mini-Boss &cThe Watcher &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7This strange creature is roaming the catacombs"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7to add powerful adventurers to its collection."));
        } else if (floor == 1) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bTiny"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cBonzo &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cBonzo &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Involved in the dark arts due to his parents'"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7insistence. Originally worked as a circus clown."));
        } else if (floor == 2) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bSmall"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cScarf &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cScarf &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7First of his class. His teacher said"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7he will do 'Great Things'."));
        } else if (floor == 3) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bSmall"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cThe Professor &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cThe Professor &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Despite his great technique, he failed the"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7masters exam three times. Works from 8-5."));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cares about his students."));
        } else if (floor == 4) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bSmall"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cThorn &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cThorn &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Powerful necromancer that specializes in"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7animals. Calls himself a vegetarian, go figure."));
        } else if (floor == 5) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bMedium"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cLivid &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cLivid &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Strongly believes he will become the Lord one"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7day. Subject of mockeries, even from his"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7disciples."));
        } else if (floor == 6) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bMedium"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cSadan &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cSadan &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Necromancy was always strong in his family"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Says he once beat a wither in a duel. Likes"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7to brag."));
        } else if (floor == 7) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Party Size: &11-5"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Dungeon Size: &bLarge"));
            lore.add(" ");
            if (unlocked) {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cMaxor, Storm, Goldor, and Necron &a✓"));
            } else {
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Boss " + add + "&cMaxor, Storm, Goldor, and Necron &4ㄨ"));
            }
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Disciples of the wither king. Inherited the"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7catacombs eons ago. Never defeated, feared by"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7anything living AND dead."));
        }
        lore.add(" ");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Requirements:"));
        if (unlocked) {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&a✓ &aCatacombs Level " + requiredLevel));
        } else {
            lore.add(ChatColor.translateAlternateColorCodes('&', "&4ㄨ &cCatacombs Level " + requiredLevel));
        }
        lore.add(" ");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&eClick to join!"));
        return lore;
    }

}