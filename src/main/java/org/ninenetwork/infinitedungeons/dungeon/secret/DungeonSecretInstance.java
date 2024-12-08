package org.ninenetwork.infinitedungeons.dungeon.secret;

import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.menu.model.SkullCreator;
import org.mineacademy.fo.model.SimpleHologramStand;
import org.mineacademy.fo.remain.Remain;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.animation.SimpleAnimatedHologram;
import org.ninenetwork.infinitedungeons.animation.SimpleAnimatedHologramSecret;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.instance.DungeonRoomInstance;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.item.talisman.DayCrystal;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;
import org.ninenetwork.infinitedungeons.util.HologramUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DungeonSecretInstance {

    @Getter
    private Map<DungeonRoomInstance, Map<Location, SecretType>> secretInstances = new HashMap<>();

    int blessingSecrets = 0;

    private Dungeon dungeon;

    public DungeonSecretInstance(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void handleSecret(Player player, Block block) {

        SecretType secretType = this.chooseSecretType(block);
        BlessingType blessingType = this.chooseBlessingType();
        int blessingLevel = this.chooseBlessingLevel("Secret");
        ItemStack item = null;
        if (secretType != SecretType.ESSENCE) {
            Remain.sendChestOpen(block);
        }
        DungeonRoomInstance instance = findSecretRoom(block.getLocation());
        if (instance != null) {
            if (instance.getSecretMap().containsKey(block.getLocation())) {
                instance.getSecretMap().put(block.getLocation(), true);
            }
            if (!instance.isCompleted()) {
                boolean isComplete = true;
                for (Map.Entry<Location, Boolean> entry : instance.getSecretMap().entrySet()) {
                    Location key = entry.getKey();
                    Boolean value = entry.getValue();
                    if (!entry.getValue()) {
                        isComplete = false;
                    }
                }
                if (isComplete) {
                    instance.completeRoom(dungeon, instance);
                }
            }
        }
        this.dungeon.getDungeonScore().addSecretFound();
        if (secretType == SecretType.BLESSING) {
            sendBlessingSecretMessage(player, blessingType, blessingLevel);
            item = getBlessingSecretAnimationItem(blessingType);
            this.dungeon.getBlessingManager().applyBlessing(blessingType, blessingLevel);
        } else if (secretType == SecretType.ITEM) {
            item = getSecretAnimationItem(secretType);
            HologramUtil.createItemSecretHologram("&c&lSecret Item", block.getLocation(), item, player);
            player.getInventory().addItem(item);
        } else if (secretType == SecretType.ESSENCE) {
            item = SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/bd0ebe00eeef7c2c50504f2556810421bace6ebda0b1d368fd78bb56be21bca6");
        }

        AtomicBoolean isFinished = new AtomicBoolean(false);
        final Location location = block.getLocation().add(0.5, 0.6, 0.5);

        if (item != null) {
            if (secretType == SecretType.BLESSING) {
                final SimpleHologramStand stand = new SimpleAnimatedHologram(location, item);
                stand.setGlowing(true);
                String lore = createLoreName(secretType, blessingType, blessingLevel);
                if (lore.equalsIgnoreCase("none")) {
                    stand.removeLore();
                } else {
                    stand.setLore(lore);
                }
                stand.spawn();
                final Entity entity = stand.getEntity();
                Common.runTimer(2, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!entity.isValid()) {
                            this.cancel();
                            return;
                        }

                        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16))
                            entity.getWorld().playEffect(entity.getLocation().add(0, 1.8, 0), Effect.SMOKE, BlockFace.DOWN);
                        else
                            entity.getWorld().playEffect(entity.getLocation().add(0, 1.8, 0), Effect.SMOKE, 6);

                        if (isFinished.get()) {
                            cancel();
                        }
                    }
                });
                Common.runLater(20 * 5, () -> {

                    // Remove old chest
                    stand.removeLore();
                    stand.remove();
                    isFinished.set(false);

                });
            } else if (secretType == SecretType.ESSENCE) {
                for (Player p : this.dungeon.getDungeonParty().getPlayers()) {
                    Common.tell(p, "&c" + player.getName() + " &fhas found a &7Wither Essence&f!");
                }
                block.setType(Material.AIR);
                final SimpleHologramStand stand = new SimpleAnimatedHologramSecret(location.clone().subtract(0.0, 2.0, 0.0), item);
                stand.removeLore();
                stand.spawn();
                final Entity entity = stand.getEntity();
                Common.runTimer(2, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!entity.isValid()) {
                            this.cancel();
                            return;
                        }

                        if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_16))
                            entity.getWorld().playEffect(entity.getLocation().add(0, 1.8, 0), Effect.BLAZE_SHOOT, BlockFace.DOWN);
                        else
                            entity.getWorld().playEffect(entity.getLocation().add(0, 1.8, 0), Effect.BLAZE_SHOOT, 6);

                        if (isFinished.get()) {
                            cancel();
                        }
                    }
                });
                Common.runLater(20 * 5, () -> {
                    stand.removeLore();
                    stand.remove();
                    isFinished.set(false);
                });
            }
        }
    }

    public DungeonRoomInstance findSecretRoom(Location locationCheck) {
        for (Map.Entry<DungeonRoomInstance, Map<Location, SecretType>> secrets : secretInstances.entrySet()) {
            DungeonRoomInstance instance = secrets.getKey();
            // ...
            for (Map.Entry<Location, SecretType> locationEntry : secrets.getValue().entrySet()) {
                Location location = locationEntry.getKey();
                if (location.equals(locationCheck)) {
                    return instance;
                }
            }
        }
        return null;
    }

    public void sendBlessingSecretMessage(Player blessingFinder, BlessingType blessingType, int level) {
        for (PlayerCache cache : this.dungeon.getPlayerCaches()) {
            Common.tell(cache.toPlayer(), "&6&lDUNGEON BUFF >> &e" + blessingFinder.getName() + " &ffound a &6Blessing Of " + blessingType + " &e" + GeneralUtils.toRomanNumerals(level) + "&f!");
        }
    }

    public static ItemStack getSecretAnimationItem(SecretType secretType) {
        ItemStack item = null;
        if (secretType == SecretType.BLESSING) {
            item = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/affb9f3aa1d1be96cace79574e72fb31b646cb6a2d19101ea127f920efe099");
        } else if (secretType == SecretType.ITEM) {
            item = getSecretItemReward();
        }
        return item;
    }

    public static ItemStack getBlessingSecretAnimationItem(BlessingType blessingType) {
        ItemStack item = null;
        if (blessingType == BlessingType.LIFE) {
            item = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/84e4a697f7f35baa4cd73429b15bec68cc728e9f6d6f0357a3833266c4c2");
        } else if (blessingType == BlessingType.POWER) {
            item = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/665cd3a24f193371eebac9a71c48f408a935afc4b435f1fb7b9843e6587298f");
        } else if (blessingType == BlessingType.STONE) {
            item = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/5ac91ab25836d13483864cddbd432dbb22d4ac293f1c99fb28919cf39c3");
        } else if (blessingType == BlessingType.WISDOM) {
            item = SkullCreator.itemFromUrl("https://textures.minecraft.net/texture/affb9f3aa1d1be96cace79574e72fb31b646cb6a2d19101ea127f920efe099");
        }
        return item;
    }

    public SecretType chooseSecretType(Block block) {
        Random rand = new Random();
        int choice = 0;
        if (block.getType() == Material.CHEST) {
            choice = rand.nextInt(2);
        } else {
            choice = 2;
        }
        //choice = 0;
        if (choice == 0) {
            return SecretType.BLESSING;
        } else if (choice == 1) {
            return SecretType.ITEM;
        } else {
            return SecretType.ESSENCE;
        }
    }

    public BlessingType chooseBlessingType() {
        Random rand = new Random();
        int choice = rand.nextInt(4);
        if (choice == 0) {
            return BlessingType.LIFE;
        } else if (choice == 1) {
            return BlessingType.POWER;
        } else if (choice == 2) {
            return BlessingType.STONE;
        } else {
            return BlessingType.WISDOM;
        }
    }

    public int chooseBlessingLevel(String blessingOrigin) {
        if (blessingOrigin.equalsIgnoreCase("Mob") || blessingOrigin.equalsIgnoreCase("Puzzle")) {
            return 5;
        } else {
            Random rand = new Random();
            int choice = rand.nextInt(2);
            return choice + 1;
        }
    }

    public String createLoreName(SecretType secretType, BlessingType blessingType, int blessingLevel) {
        if (secretType == SecretType.BLESSING) {
            if (blessingType == BlessingType.LIFE) {
                return "&c&lBlessing of Life &f" + GeneralUtils.toRomanNumerals(blessingLevel);
            } else if (blessingType == BlessingType.POWER) {
                return "&6&lBlessing of Power &f" + GeneralUtils.toRomanNumerals(blessingLevel);
            } else if (blessingType == BlessingType.STONE) {
                return "&7&lBlessing of Stone &f" + GeneralUtils.toRomanNumerals(blessingLevel);
            } else if (blessingType == BlessingType.WISDOM) {
                return "&d&lBlessing of Wisdom &f" + GeneralUtils.toRomanNumerals(blessingLevel);
            }
        }
        return "none";
    }

    public static ItemStack getSecretItemReward() {
        ArrayList<ItemStack> rewards = new ArrayList<>();
        rewards.add(CustomDungeonItemManager.getInstance().getHandler(DayCrystal.class).getItem(false, ""));
        Random rand = new Random();
        return rewards.get(0);
    }

    public void initializeAllDungeonSecrets() {
        Location roomCenter;
        Location instanceCenter;
        Location secretLocation = null;
        int totalSecrets = 0;
        int orientation;
        double x;
        double y;
        double z;
        double xx;
        double yy;
        double zz;
        double xDifferenceFrom0 = 0;
        double yDifferenceFrom0 = 0;
        double zDifferenceFrom0 = 0;
        double yfinal = 0;
        String xBaseType = "none";
        String yBaseType = "none";
        String zBaseType = "none";
        for (DungeonRoomInstance instance : dungeon.getDungeonRooms()) {
            Common.log("Attempting to search a room for secrets");
            if (instance.getType() == DungeonRoomType.BLOODRUSH && !instance.getDungeonRoom().getSecrets().isEmpty()) {
                Common.log("room has secrets");
                if (!secretInstances.containsKey(instance)) {
                    Map<Location, SecretType> m = new HashMap<>();
                    secretInstances.put(instance, m);
                }
                orientation = instance.getOrientation();
                roomCenter = instance.getDungeonRoom().getRoomCenter();
                instanceCenter = instance.getShape().getRoomCenterLocation();
                xx = instanceCenter.getX();
                yy = instanceCenter.getY();
                zz = instanceCenter.getZ();
                x = roomCenter.getX();
                y = roomCenter.getY();
                z = roomCenter.getZ();
                for (Location location : instance.getDungeonRoom().getSecrets()) {
                    if (location.getX() < x) {
                        xDifferenceFrom0 = Math.floor(x) - Math.floor(location.getX());
                        xBaseType = "subtract";
                    } else if (location.getX() == x) {
                        xDifferenceFrom0 = 0;
                        xBaseType = "none";
                    } else if (location.getX() > x) {
                        xDifferenceFrom0 = Math.floor(location.getX()) - Math.floor(x);
                        xBaseType = "add";
                    }

                    if (location.getY() < y) {
                        yDifferenceFrom0 = Math.floor(y) - Math.floor(location.getY());
                        yfinal = -yDifferenceFrom0;
                    } else if (location.getY() == y) {
                        yDifferenceFrom0 = 0;
                        yfinal = 0;
                    } else if (location.getY() > y) {
                        yDifferenceFrom0 = Math.floor(location.getY()) - Math.floor(y);
                        yfinal = yDifferenceFrom0;
                    }

                    if (location.getZ() < z) {
                        zDifferenceFrom0 = Math.floor(z) - Math.floor(location.getZ());
                        zBaseType = "subtract";
                    } else if (location.getZ() == z) {
                        zDifferenceFrom0 = 0;
                        zBaseType = "none";
                    } else if (location.getZ() > z) {
                        zDifferenceFrom0 = Math.floor(location.getZ()) - Math.floor(z);
                        zBaseType = "add";
                    }

                    if (orientation == 0) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } //else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("add")) {

                        //} else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("subtract")) {

                        //} else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("none")) {

                        //} else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("none")) {

                        //} else if (xBaseType.equalsIgnoreCase("none") && zBaseType.equalsIgnoreCase("none")) {

                        //}
                    } else if (orientation == 1) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        }
                    } else if (orientation == 2) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz + zDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx - xDifferenceFrom0, yy + yfinal, zz - zDifferenceFrom0);
                        }
                    } else if (orientation == 3) {
                        if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("subtract") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz + xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("subtract")) {
                            secretLocation = new Location(location.getWorld(), xx - zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        } else if (xBaseType.equalsIgnoreCase("add") && zBaseType.equalsIgnoreCase("add")) {
                            secretLocation = new Location(location.getWorld(), xx + zDifferenceFrom0, yy + yfinal, zz - xDifferenceFrom0);
                        }
                    }
                    Common.log("Adding secret to " + secretLocation.getX() + "," + secretLocation.getY() + "," + secretLocation.getZ());
                    totalSecrets = totalSecrets + 1;
                    instance.addSecretToMap(secretLocation);
                    Random rand = new Random();
                    ArrayList<SecretType> secretTypes = new ArrayList<>(Arrays.asList(SecretType.ESSENCE, SecretType.BLESSING, SecretType.ITEM));
                    SecretType chosen = secretTypes.get(rand.nextInt(3));

                    if (chosen == SecretType.BLESSING) {
                        this.secretInstances.get(instance).put(secretLocation, SecretType.BLESSING);
                        secretLocation.getBlock().setType(Material.CHEST);
                    } else if (chosen == SecretType.ESSENCE) {
                        this.secretInstances.get(instance).put(secretLocation, SecretType.ESSENCE);
                        SkullCreator.blockWithUrl(secretLocation.getBlock(), "http://textures.minecraft.net/texture/bd0ebe00eeef7c2c50504f2556810421bace6ebda0b1d368fd78bb56be21bca6");
                    } else if (chosen == SecretType.ITEM) {
                        this.secretInstances.get(instance).put(secretLocation, SecretType.ITEM);
                        secretLocation.getBlock().setType(Material.CHEST);
                    }

                }
            }
        }
        this.dungeon.getDungeonScore().setTotalSecrets(totalSecrets);
    }

}