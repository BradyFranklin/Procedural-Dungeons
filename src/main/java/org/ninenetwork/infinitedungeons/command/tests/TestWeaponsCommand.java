package org.ninenetwork.infinitedungeons.command.tests;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.item.CustomDungeonItemManager;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorBoots;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorChestplate;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorHelmet;
import org.ninenetwork.infinitedungeons.item.armor.goldor.GoldorLeggings;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronBoots;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronChestplate;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronHelmet;
import org.ninenetwork.infinitedungeons.item.armor.necron.NecronLeggings;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinBoots;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinChestplate;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinHelmet;
import org.ninenetwork.infinitedungeons.item.armor.silentassassin.SilentAssassinLeggings;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormBoots;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormChestplate;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormHelmet;
import org.ninenetwork.infinitedungeons.item.armor.storm.StormLeggings;
import org.ninenetwork.infinitedungeons.item.talisman.DayCrystal;
import org.ninenetwork.infinitedungeons.item.talisman.SpeedTalisman;
import org.ninenetwork.infinitedungeons.item.weapons.Hyperion;
import org.ninenetwork.infinitedungeons.item.weapons.Terminator;

public class TestWeaponsCommand extends SimpleCommand {

    public TestWeaponsCommand() {
        super("weapon");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            ItemStack item = CustomDungeonItemManager.getInstance().getHandler(Hyperion.class).getItem(false, "");
            ItemStack item1 = CustomDungeonItemManager.getInstance().getHandler(Terminator.class).getItem(false, "");
            ItemStack item2 = CustomDungeonItemManager.getInstance().getHandler(StormHelmet.class).getItem(true, "http://textures.minecraft.net/texture/7c01b7cf7dc9a8ae2c9cbce5d0ddeba1ce697f22f634d7e19ad57e8619bccb08");
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(StormChestplate.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(StormLeggings.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(StormBoots.class).getItem(false, ""));

            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(NecronHelmet.class).getItem(true, "http://textures.minecraft.net/texture/d39fde26fa3e045239c5723c893a6b560ca065be081f74b167af973c78e4ad0e"));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(NecronChestplate.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(NecronLeggings.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(NecronBoots.class).getItem(false, ""));

            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(SilentAssassinHelmet.class).getItem(true, "http://textures.minecraft.net/texture/1c15bb1acf062f2d147ae17ca9b098d8c88f48e94e5f515afbc79f03a6898a4"));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(SilentAssassinChestplate.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(SilentAssassinLeggings.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(SilentAssassinBoots.class).getItem(false, ""));

            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(GoldorHelmet.class).getItem(true, "http://textures.minecraft.net/texture/6481f1d5c6151a96ad879e00bb491f4dc604e5ebf5d9ac3b8cd9642272f21c16"));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(GoldorChestplate.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(GoldorLeggings.class).getItem(false, ""));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(GoldorBoots.class).getItem(false, ""));

            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(SpeedTalisman.class).getItem(true, "http://textures.minecraft.net/texture/f06706eecb2d558ace27abda0b0b7b801d36d17dd7a890a9520dbe522374f8a6"));
            player.getInventory().addItem(CustomDungeonItemManager.getInstance().getHandler(DayCrystal.class).getItem(false, ""));

            player.getInventory().addItem(item);
            player.getInventory().addItem(item2);
            player.getInventory().addItem(item1);
        }
    }

}