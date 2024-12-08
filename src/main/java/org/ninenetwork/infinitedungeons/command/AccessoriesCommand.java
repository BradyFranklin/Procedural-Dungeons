package org.ninenetwork.infinitedungeons.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.util.AccessoryUtil;

import java.util.ArrayList;

public class AccessoriesCommand extends SimpleCommand {

    public AccessoriesCommand() {
        super("accessory");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        Player player = (Player) sender;
        ArrayList<ItemStack> vaultItems = AccessoryUtil.getItems(player);
        Inventory vault = Bukkit.createInventory(player, 54, "Accessory Bag");
        vaultItems.stream().forEach(vault::addItem);
        player.openInventory(vault);
    }

}