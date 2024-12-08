package org.ninenetwork.infinitedungeons;

import org.bukkit.inventory.ShapedRecipe;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.ninenetwork.infinitedungeons.animation.Effects;
import org.ninenetwork.infinitedungeons.command.*;
import org.ninenetwork.infinitedungeons.command.admin.*;
import org.ninenetwork.infinitedungeons.command.tests.*;
import org.ninenetwork.infinitedungeons.dungeon.Dungeon;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;
import org.ninenetwork.infinitedungeons.item.CustomRecipe;
import org.ninenetwork.infinitedungeons.item.weapons.Hyperion;
import org.ninenetwork.infinitedungeons.item.weapons.Terminator;
import org.ninenetwork.infinitedungeons.listener.DamageListener;
import org.ninenetwork.infinitedungeons.listener.DungeonListener;
import org.ninenetwork.infinitedungeons.listener.NPCListener;
import org.ninenetwork.infinitedungeons.listener.StatListener;
import org.ninenetwork.infinitedungeons.listener.armorevent.ArmorEquipEvent;
import org.ninenetwork.infinitedungeons.mob.DungeonMobRegistry;
import org.ninenetwork.infinitedungeons.task.ActionBarTask;
import org.ninenetwork.infinitedungeons.task.DungeonScoringTask;
import org.ninenetwork.infinitedungeons.task.HealthManagementTask;
import org.ninenetwork.infinitedungeons.task.ManaRegenerationTask;

public class InfiniteDungeonsPlugin extends SimplePlugin {

    @Override
    protected void onPluginStart() {

        Common.log("Infinite Dungeons Framework Initialized");

        Common.setLogPrefix("&8[&fInfiniteDungeons&8]&f");
        Common.setTellPrefix("&c&lDUNGEONS >> &f");

        registerCommand(new StopDungeonCommand());
        registerCommand(new SetDungeonLevelCommand());
        registerCommand(new CreateDungeonCommand());
        registerCommand(new ForceStopDungeonCommand());
        registerCommand(new SetEditingRoomCommand());
        registerCommand(new RoomCreationGuiCommand());
        registerCommand(new DungeonForceHealCommand());

        registerCommand(new AccessoriesCommand());

        registerCommand(new TestPathfindingNpc());
        registerCommand(new TestRemoveSecrets());
        registerCommand(new TestBossCommand());
        registerCommand(new TestEnchanting());
        registerCommand(new TestCommand());
        registerCommand(new TestTwoCommand());
        registerCommand(new TestThreeCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new DungeonFinderCommand());
        registerCommand(new SetBlockCommand());
        registerCommand(new CreateRoomCommand());
        registerCommand(new TestDefaultRoomCreator());
        registerCommand(new InvitePlayerCommand());
        registerCommand(new JoinPartyCommand());
        registerCommand(new TestWeaponsCommand());
        registerCommand(new TestSetFloorHeight());
        registerCommand(new CreateNewRoomSchematics());
        registerCommand(new CreateOrEditRoom());
        registerCommand(new TestDungeonScoreCommand());

        registerEvents(new DungeonListener());
        registerEvents(new NPCListener());
        registerEvents(new DamageListener());
        registerEvents(new Hyperion(this));
        registerEvents(new Terminator(this));
        registerEvents(new StatListener());

        ArmorEquipEvent.registerListener(this);
        registerAllCommands(NMSCommand.class);

        if (HookManager.isCitizensLoaded()) {
            //CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ShadowAssassinTeleportTrait.class).withName("shadow-teleport"));
            //SentinelPlugin.instance.registerIntegration(new SentinelDungeon());
        }

        for (ShapedRecipe recipe : CustomRecipe.getRecipes()) {
            getServer().addRecipe(recipe);
        }

    }

    @Override
    protected void onReloadablesStart() {

        Common.log("Infinite Dungeons Framework Restarting");

        if (Common.doesPluginExist("EffectLib")) {
            Effects.load();
        }

        Dungeon.loadDungeons();
        DungeonRoom.loadDungeonRooms();
        DungeonMobRegistry.getInstance().initializeMobTypes();

        Common.runTimer(20, new ActionBarTask());
        Common.runTimer(100, new DungeonScoringTask());
        Common.runTimer(40, new HealthManagementTask());
        Common.runTimer(20, new ManaRegenerationTask());

    }

    @Override
    protected void onPluginReload() {
        if (Common.doesPluginExist("EffectLib")) {
            Effects.disable();
        }
    }

    @Override
    protected void onPluginStop() {
        DungeonMobRegistry.getInstance().emergencyClearAllMobs();
        Common.log("Infinite Dungeons Framework Shutting Down");
    }

    public static InfiniteDungeonsPlugin getInstance() {
        return getPlugin(InfiniteDungeonsPlugin.class);
    }

    // Finish Main Menu Extensions

    // Need a singleton mapping system to store all stat boosts of all kinds including percentage based ones or value based etc.

}