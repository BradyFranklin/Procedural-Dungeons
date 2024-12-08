package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleScoreboard;

import java.util.ArrayList;
import java.util.List;

public class DungeonScoreboard extends SimpleScoreboard {

    private final Dungeon dungeon;

    public DungeonScoreboard(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.setTitle("&8-------" + dungeon.getName() + "&8-------");
        this.setTheme(ChatColor.RED, ChatColor.WHITE);
        this.setUpdateDelayTicks(20);
    }

    @Override
    protected String replaceVariables(final Player player, String message) {

        message = Replacer.replaceArray(message,
                "remaining_start", !this.dungeon.getStartCountdown().isRunning() ? "Waiting"
                        : Common.plural(this.dungeon.getStartCountdown().getTimeLeft(), "second"),

                "remaining_end", TimeUtil.formatTimeShort(this.dungeon.getHeartbeat().getTimeLeft()), // -> 1m5s 1m4s
                "players", this.dungeon.getPlayers(this.dungeon.getState() == DungeonState.EDITED ? DungeonJoinMode.EDITING : DungeonJoinMode.PLAYING /* ignore spectators */).size(),
                "state", ItemUtil.bountifyCapitalized(this.dungeon.getState()), // PLAYED -> Played
                "lobby_set", this.dungeon.getLobbyLocation() != null,
                "region_set", this.dungeon.getRegion().isWhole());

        return message.replace("true", "&ayes").replace("false", "&4no");
    }

    public void onPlayerJoin(final Player player) {
        this.show(player);
    }

    public void onPlayerLeave(final Player player) {
        if (this.isViewing(player))
            this.hide(player);
    }

    public void onLobbyStart() {
        this.addRows("",
                "Players: {players}",
                "Starting in: {remaining_start}",
                "State: {state}");
    }

    public final void onEditStart() {
        this.addRows("",
                "Editing players: {players}",
                "",
                "Lobby: {lobby_set}",
                "Region: {region_set}");

        this.addRows(this.onEditLines());
        this.addRows("",
                "&7Use: /game tools to edit.");
    }

    protected List<Object> onEditLines() {
        return new ArrayList<>();
    }

    public void onGameStart() {
        this.removeRow("Starting in");
        this.addRows("Time left: {remaining_end}");
    }

    public void onGameStop() {
        this.clearRows();
        this.stop();
    }

    public void onSpectateStart() {
    }

}