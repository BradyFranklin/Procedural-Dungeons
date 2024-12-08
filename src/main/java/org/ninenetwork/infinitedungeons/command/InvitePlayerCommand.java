package org.ninenetwork.infinitedungeons.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.party.DungeonPartyOld;

public class InvitePlayerCommand extends SimpleCommand {

    public InvitePlayerCommand() {
        super("invite");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player receiver = Bukkit.getPlayer(args[0]);
            DungeonPartyOld.invitePlayerToParty(player, receiver);

        }
    }

}