package org.ninenetwork.infinitedungeons.command.tests;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.menu.DungeonClassMenu;

public class TestTwoCommand extends SimpleCommand {

    public TestTwoCommand() {
        super("testing");
    }

    @Override
    protected void onCommand() {
        checkConsole();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerCache cache = PlayerCache.from(player);
            Common.tell(player,"Creating Party");
            //DungeonParty.addPlayerToParty("InfinitySB2", Bukkit.getPlayer("Frypez"));
            new DungeonClassMenu().displayTo(player);
            CitizensAPI.getNPCRegistry().despawnNPCs(DespawnReason.REMOVAL);
            CitizensAPI.getNPCRegistry().deregisterAll();
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            player.setHealth(40.0);
            player.setHealthScaled(true);
            player.setHealthScale(40.0);
        }
    }

}