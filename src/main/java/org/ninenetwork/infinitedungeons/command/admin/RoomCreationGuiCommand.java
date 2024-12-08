package org.ninenetwork.infinitedungeons.command.admin;

import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.menu.admin.RoomCreationMenu;

public class RoomCreationGuiCommand extends SimpleCommand {

    public RoomCreationGuiCommand() {
        super("dcreate");
    }

    @Override
    protected void onCommand() {
        Player player = (Player) sender;
        PlayerCache cache = PlayerCache.from(player);
        new RoomCreationMenu().displayTo(player);
    }

}