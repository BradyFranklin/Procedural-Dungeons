package org.ninenetwork.infinitedungeons.item.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.CompMetadata;
import org.ninenetwork.infinitedungeons.item.AbstractDungeonItem;
import org.ninenetwork.infinitedungeons.item.ItemLoreGenerator;
import org.ninenetwork.infinitedungeons.item.ItemType;
import org.ninenetwork.infinitedungeons.mob.DungeonMobManager;
import org.ninenetwork.infinitedungeons.playerstats.PlayerStat;
import org.ninenetwork.infinitedungeons.playerstats.damage.DamageType;
import org.ninenetwork.infinitedungeons.playerstats.damage.PlayerDamageHandler;
import org.ninenetwork.infinitedungeons.util.CooldownManager;

import java.time.Duration;
import java.util.*;

public class Terminator extends AbstractDungeonItem {

    public Terminator(SimplePlugin plugin) {
        super(plugin, "Terminator", Material.BOW);
    }

    private static final Set<Action> CLICK_ACTIONS = EnumSet.of(
            Action.RIGHT_CLICK_AIR,
            Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR,
            Action.LEFT_CLICK_BLOCK
    );

    @Override
    protected ItemStack generateItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String name = ChatColor.translateAlternateColorCodes('&', "&dTerminator &c✪✪✪✪&6✪");
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS);

        meta.setLore(ItemLoreGenerator.dungeonItemBaseLoreGenerator(Terminator.class, item));

        item.setItemMeta(meta);

        CompMetadata.setMetadata(item, "DungeonItem", "Terminator");
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        if (!isApplicable(item)) {
            return;
        }
        if (!CLICK_ACTIONS.contains(event.getAction())) {
            return;
        }
        event.setCancelled(true);

        UUID uuid = player.getUniqueId();
        CooldownManager manager = CooldownManager.getInstance();
        Duration timeLeft = manager.getRemainingCooldown(uuid, "Terminator");
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            manager.setCooldown(uuid, "Terminator", Duration.ofMillis(500L));
            new BukkitRunnable() {
                @Override
                public void run() {
                    Vector forward = player.getLocation().getDirection();
                    Vector right = forward.clone().rotateAroundY(Math.toRadians(6));
                    Vector left = forward.clone().rotateAroundY(Math.toRadians(-6));
                    shootArrow(player, forward);
                    shootArrow(player, right);
                    shootArrow(player, left);
                }
            }.runTask(plugin);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // Credit to (https://www.spigotmc.org/threads/forcing-enderman-to-take-damage-from-projectiles.511665/#post-4336602)
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) event.getEntity();
        Player shooter = null;
        if (arrow.getShooter() instanceof Player) {
            shooter = (Player) arrow.getShooter();
        }
        if (shooter == null) {
            arrow.remove();
            return;
        }
        for (Entity entity : arrow.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (DungeonMobManager.getInstance().checkIsDungeonMob(livingEntity)) {
                    PlayerDamageHandler.handleNormalDamage(shooter, livingEntity, DamageType.PROJECTILE);
                }
            }
        }
        arrow.remove();
        event.setCancelled(true);
    }

    private void shootArrow(Player player, Vector direction) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(PlayerDamageHandler.chooseIfCrit(player));
        arrow.setVelocity(direction.multiply(4));
    }

    @Override
    public LinkedHashMap<PlayerStat, Double> itemStatData() {
        LinkedHashMap<PlayerStat, Double> statData = new LinkedHashMap<>();
        statData.put(PlayerStat.DAMAGE, 310.0);
        statData.put(PlayerStat.STRENGTH, 50.0);
        statData.put(PlayerStat.CRIT_DAMAGE, 350.0);
        statData.put(PlayerStat.BONUS_ATTACK, 40.0);
        //statData.put(PlayerStat.MANACOST, 210.0);
        return statData;
    }

    @Override
    public List<String> itemLoreDescription() {
        return Arrays.asList("&7Shoots &b3 &7arrows at once.", "&7Can damage enderman.", " ", "&cDivides your &9crit chance &cby 4!");
    }

    @Override
    public String itemRarityDescription() {
        return "&d&l&k%&d&lMYTHIC DUNGEON BOW &d&l&k%";
    }

    @Override
    protected boolean hasAbilityOrBonus() {
        return true;
    }

    @Override
    public List<String> itemAbilityDescription() {
        return Arrays.asList("&6Ability: Salvation &e&lRIGHT CLICK", "&7Can be casted after landing &63 &7hits.", "&7Shoot a beam, penetrating up",
                "&7to &e5 &7foes and dealing &c2x", "&7the damage an arrow would.", "&7The beam always crits.");
    }

    @Override
    public ItemType getItemType() {
        return ItemType.ABILITY_WEAPON;
    }

    @Override
    protected ArrayList<String> adaptiveAdditions() {
        return null;
    }

}