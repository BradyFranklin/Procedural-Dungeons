package org.ninenetwork.infinitedungeons.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.party.DungeonPartyOld;

public class JoinPartyCommand extends SimpleCommand {

    public JoinPartyCommand() {
        super("join");
        setMinArguments(1);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Player receiver = Bukkit.getPlayer(args[0]);
            DungeonPartyOld.joinPlayerToParty(receiver, player);
        }
    }

}