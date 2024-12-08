package org.ninenetwork.infinitedungeons.command;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.MinecraftVersion;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.HookManager;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.roomtools.DungeonRoomCreation;

public class CreateOrEditRoom extends SimpleCommand {

    public CreateOrEditRoom() {
        super("droom");
        setMinArguments(3);
    }

    @Override
    protected void onCommand() {
        checkConsole();
        checkBoolean(HookManager.isWorldEditLoaded(), "Loading or saving schematic requires WorldEdit.");
        checkBoolean(MinecraftVersion.atLeast(MinecraftVersion.V.v1_13), "Loading or saving schematic requires Minecraft 1.13 or greater.");
        Player player = (Player) sender;
        String param = args[0];
        String name = args[1];
        String sizeIdentifier = args[2];

        final DungeonRoomType type = this.findEnum(DungeonRoomType.class, param.toUpperCase(), "No such room type '{0}'. Available: {available}");
        DungeonRoomCreation.createDungeonRoom(player, name, type, sizeIdentifier);
        PlayerCache.from(player).setDungeonRoomEditing(name);
    }

}