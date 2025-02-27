package org.ninenetwork.infinitedungeons.dungeon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum DungeonStopReason {

    NOT_ENOUGH_PLAYERS("{game} lacked enough\n players to start! (&c{players}&7/{min_players})"),
    ERROR("Unknown error occured, please contact the administrator!"),
    LAST_PLAYER_LEFT,
    TIMEOUT("{game} ran out of time and was ended!"),
    COMMAND("{game} was stopped by an administrator!"),
    RELOAD("Server reloaded, stopping game for safety."),
    GAMERS_DISCONNECTED,
    BEDWARS_WIN,
    BEDWARS_TEAM_WIN;

    @Getter
    private String message;
}