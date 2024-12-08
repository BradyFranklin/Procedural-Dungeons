package org.ninenetwork.infinitedungeons.prompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.remain.CompSound;
import org.ninenetwork.infinitedungeons.PlayerCache;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoom;
import org.ninenetwork.infinitedungeons.dungeon.DungeonRoomType;
import org.ninenetwork.infinitedungeons.dungeon.roomtools.DungeonRoomCreation;

import javax.annotation.Nullable;

import static org.mineacademy.fo.ReflectionUtil.lookupEnum;

public class RoomCreationNamePrompt extends SimplePrompt {

    @Override
    protected String getPrompt(ConversationContext context) {
        return "&c&lDUNGEONS >> &fWhat would you like to name this room?";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        if (input.length() > 18) {
            return false;
        }
        if (DungeonRoom.findByName(input) != null) {
            return false;
        }

        return true;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "&c&lDUNGEONS >> &fEither a room of that name exists already or input was too long. Please try again.";
    }

    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        Player player = this.getPlayer(context);
        PlayerCache cache = PlayerCache.from(player);
        final DungeonRoomType type = lookupEnum(DungeonRoomType.class, cache.getRoomCreatorTypeIdentifier(), "Failed to find a valid room type associated with room creation.");
        DungeonRoomCreation.createDungeonRoom(player, input, type, cache.getRoomCreatorSizeIdentifier());
        CompSound.LEVEL_UP.play(player);
        tell("&c&lDUNGEONS >> &fCreated &c" + cache.getRoomCreatorSizeIdentifier() + " &fRoom &c" + input + " &fof Type &c" + cache.getRoomCreatorTypeIdentifier() + " &fsuccessfully!");
        return END_OF_CONVERSATION;
    }

}