package org.ninenetwork.infinitedungeons.item.weapons;

import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.playerstats.ManaManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.damage.DamageType;
import org.ninenetwork.infinitedungeons.playerstats.damage.PlayerDamageHandler;
import org.ninenetwork.infinitedungeons.settings.Settings;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.*;

public class Hyperion extends AbstractDungeonItem {

    public Hyperion(SimplePlugin plugin) {
        super(plugin, "Hyperion", Material.IRON_SWORD);
    }

    @Override
    protected ItemStack generateItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', Settings.Items.HYPERIONNAME);
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(Hyperion.class, item));

        item.setItemMeta(meta);

        /*
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound stats = nbtItem.addCompound("dungeonstats");
        stats.setDouble("dungeondamage", 260.0);
        ItemStack completeItem = NBT.itemStackFromNBT(nbtItem);

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.setDouble("DungeonDamage", 260.0);
        nbt.setDouble("DungeonStrength", 150.0);
        ItemStack completeItem = NBT.itemStackFromNBT(nbt);


        NBT.modify(item, nbt -> {
            nbt.setDouble("Damage", 260.0);
            nbt.setDouble("Strength", 150.0);
            nbt.setDouble("Intelligence", 350.0);
            nbt.setDouble("Ferocity", 30.0);
            nbt.setDouble("ManaCost", 210.0);
        });
        */

        CompMetadata.setMetadata(item, "DungeonItem", "Hyperion");
        CompMetadata.setMetadata(item, "WitherScroll", "false");
        CompMetadata.setMetadata(item, "ImpactScroll", "false");
        CompMetadata.setMetadata(item, "ShadowScroll", "false");
        return item;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!isApplicable(item)) {
            return;
        }
        if (player.getWorld().getName().equals(Settings.PluginServerSettings.DUNGEON_WORLD_NAME)) {
            if (ManaManager.useMana(player, getBaseManaCost())) {
                final HashSet hashSet = new HashSet<Material>();
                hashSet.add(Material.AIR);

                Vector direction = player.getLocation().getDirection();

                final Block block = player.getTargetBlock((Set<Material>) hashSet, 10);

                final Location playerLocation = player.getLocation();
                final Location teleportLocation = new Location(block.getWorld(), block.getX(),
                        block.getY(), block.getZ(), playerLocation.getYaw(), playerLocation.getPitch());

                if (teleportLocation.getBlock().getType() != Material.AIR) {
                    Vector v = teleportLocation.getDirection().normalize().multiply(-1);
                    teleportLocation.subtract(0, 0, 0).add(v);
                    if (teleportLocation.clone().add(0.0, 0.6, 0.0).getBlock().getType() == Material.AIR) {
                        teleportLocation.setY(teleportLocation.getY() + 0.6);
                    }

                }

                if (new Location(teleportLocation.getWorld(), teleportLocation.getX(), teleportLocation.getY() + 1,
                        teleportLocation.getZ()).getBlock().getType() == Material.AIR
                        && player.getLocation().add(player.getLocation().getDirection()).getBlock().getType() == Material.AIR) {
                    teleportLocation.setX(teleportLocation.getX() - 0.5D);
                    teleportLocation.setZ(teleportLocation.getZ() - 0.5D);
                } else {
                    teleportLocation.setX(teleportLocation.getX() + 0.5D);
                    teleportLocation.setZ(teleportLocation.getZ() + 0.5D);
                }


                player.teleport(teleportLocation);

                final double radius = 6;

                int enemies = 0;

                double damage = 0.0;

                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity instanceof LivingEntity) {
                        if (DungeonMobManager.getInstance().checkIsDungeonMob((LivingEntity) entity)) {
                            damage += PlayerDamageHandler.handleNormalDamage(player, (LivingEntity) entity, DamageType.MAGIC);
                        } else if (entity instanceof NPC && ((NPC) entity).hasTrait(SentinelTrait.class)) {
                            damage += PlayerDamageHandler.handleNormalDamage(player, (LivingEntity) entity, DamageType.MAGIC);
                        }
                    }
                }

            /*
            for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity) {
                    final LivingEntity livingEntity = (LivingEntity) entity;
                    if (livingEntity.equals(player) || !DungeonMobRegistry.getInstance().checkMobIsRegistered(entity)) {
                        continue;
                    }
                    livingEntity.damage(damage);
                    Common.tell(player, "Would Deal " + PlayerDamageHandler.mainDamageSequence(DamageType.MELEE, player, livingEntity, player.getEquipment().getItemInMainHand(), Hyperion.class));
                    enemies++;
                } else if (entity instanceof NPC && ((NPC) entity).hasTrait(SentinelTrait.class)) {
                    NPC npc = (NPC) entity;
                    SentinelTrait sentinel = npc.getOrAddTrait(SentinelTrait.class);
                    sentinel.setHealth(sentinel.health - damage);
                }
            }
            */

                if (enemies > 0) {
                    final String damageString = String.format("%,d", enemies * (int) damage);
                    Common.tell(player, String.format("&7Your Implosion hit &c%1$s &7enemies for &c%2$s.00 &7damage.", enemies, damageString));
                }
            }
        }
    }

    private static Location getTeleportLocation(Player player, Vector direction) {
        for (int i = 0; i <= 10; i++) {
            Location targetLocation = player.getLocation().clone().add(direction.multiply(i));
            Block targetBlock = targetLocation.getBlock();

            if (targetBlock.getType() == Material.AIR) {
                return targetLocation;
            }

            Location blockBehind = targetLocation.clone().subtract(direction);
            Block blockBehindTarget = blockBehind.getBlock();

            if (blockBehindTarget.getType() == Material.AIR) {
                return blockBehind;
            }
        }
        return null;
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.DAMAGE, 260.0);
        statData.put(PlayerStat.STRENGTH, 150.0);
        statData.put(PlayerStat.FEROCITY, 30.0);
        statData.put(PlayerStat.INTELLIGENCE, 350.0);
        //statData.put(PlayerStat.MANACOST, 210.0);
        return statData;
    }

    public static double getBaseManaCost() {
        return 120.0;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7Deals +&c50% &7damage to", "&7Withers. Grants &c+1" + GeneralUtils.getStatSymbol(PlayerStat.DAMAGE)
                + " Damage", "&7and &a+2&b" + GeneralUtils.getStatSymbol(PlayerStat.INTELLIGENCE) + " Intelligence", "&7per &cCatacombs &7level.");
    }

    @Override
    public String itemRarityDescription() {
        return "&d&l&k% &d&lMYTHIC DUNGEON SWORD &d&l&k%";
    }

    @Override
    protected boolean hasAbilityOrBonus() {
        return true;
    }

    @Override
    public List<String> itemAbilityDescription() {
        return Arrays.asList("&aScroll Abilities", "&7None");
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ABILITY_WEAPON;
    }

    @Override
    protected ArrayList<String> adaptiveAdditions() {
        return null;
    }

    @Override
    public double abilityDamage() {
        return 10000.0;
    }

    @Override
    public double abilityScaling() {
        return 0.3;
    }

}