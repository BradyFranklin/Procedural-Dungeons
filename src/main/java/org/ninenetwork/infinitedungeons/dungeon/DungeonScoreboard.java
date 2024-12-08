package org.ninenetwork.infinitedungeons.dungeon;

import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleScoreboard;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

import java.util.ArrayList;
import java.util.List;

public class DungeonScoreboard extends SimpleScoreboard {

    private final Dungeon dungeon;

    public DungeonScoreboard(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.setTitle("&8-------" + "&#00C1FF&lD&#0CB1FA&lu&#17A1F5&ln&#2391F0&lg&#2E81EC&le&#3A71E7&lo&#4561E2&ln&#5151DD&ls" + "&8-------");
        //this.setTheme(ChatColor.RED, ChatColor.WHITE);
        this.setUpdateDelayTicks(20);
    }

    @Override
    protected String replaceVariables(final Player player, String message) {

        message = Replacer.replaceArray(message,
                "remaining_start", !this.dungeon.getStartCountdown().isRunning() ? "Waiting"
                        : Common.plural(this.dungeon.getStartCountdown().getTimeLeft(), "second"),

                "remaining_end", TimeUtil.formatTimeShort(this.dungeon.getHeartbeat().getTimeLeft()), // -> 1m5s 1m4s
                "dungeon_type", GeneralUtils.getDungeonTypeName(this.dungeon.getType()) + " " + "&7F" + this.dungeon.getFloor(),
                "dungeon_time", GeneralUtils.dungeonTimeFormatter(this.dungeon.getDungeonTimeTracker().getElapsedTime()),
                "dungeon_score", this.dungeon.getDungeonScore().getDungeonScore(),
                "dungeon_score_projection", this.dungeon.getDungeonScore().getDungeonScoreProjection(),
                "dungeon_clear", this.dungeon.getDungeonScore().getPercentageCleared() + "%",
                "health", player.getHealth(),
                "players", this.dungeon.getPlayerCaches(this.dungeon.getState() == DungeonState.EDITED ? DungeonJoinMode.EDITING : DungeonJoinMode.PLAYING /* ignore spectators */).size(),
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

    public void onDungeonStart() {
        this.setTitle(Common.colorize(getTitle(dungeon.getType(), dungeon.getFloor())));
        //this.removeRow("Starting in");
        this.addRows("&c{dungeon_type}");
        this.addRows("&fTime Elapsed: &a{dungeon_time}");
        this.addRows("&fCleared: &a{dungeon_clear}");
        this.addRows("&fScore: {dungeon_score}");
        this.addRows("&fProjected Score: {dungeon_score_projection}");
        this.addRows(" ");
        //This probably will say all players health's are the same only taking in the scoreboard owners'
        for (PlayerCache cache : this.dungeon.getPlayerCaches()) {
            this.addRows("&e" + GeneralUtils.scoreboardClassSymbol(cache.getCurrentDungeonClass()) + " &7" + cache.toPlayer().getName() + " &a" + (int) cache.getActiveHealth() + "&7HP");
        }
    }

    private static String getTitle(DungeonType type, int floor) {
        String title = " ";
        if (type == DungeonType.CATACOMBS) {
            title = "&#FF00E3&lD&#F90DE4&lu&#F41BE6&ln&#EE28E7&lg&#E836E9&le&#E343EA&lo&#DD51EC&ln&#D75EED&ls &#D26BEF&l| &#CC79F0&lC&#C686F2&la&#C094F3&lt&#BBA1F5&la&#B5AEF6&lc&#AFBCF8&lo&#AAC9F9&lm&#A4D7FB&lb&#9EE4FC&ls &#99F2FE&lF&#93FFFF&l" + floor;
        } else if (type == DungeonType.MASTER) {
            title = "&#FF00E3&lD&#F810E5&lu&#F220E7&ln&#EB30E8&lg&#E440EA&le&#DD50EC&lo&#D760EE&ln&#D070EF&ls &#C980F1&l| &#C28FF3&lM&#BC9FF5&la&#B5AFF6&ls&#AEBFF8&lt&#A7CFFA&le&#A1DFFC&lr &#9AEFFD&lF&#93FFFF&l" + floor;
        }
        return title;
    }

    public void onDungeonStop() {
        this.clearRows();
        this.stop();
    }

    public void onSpectateStart() {
    }

}