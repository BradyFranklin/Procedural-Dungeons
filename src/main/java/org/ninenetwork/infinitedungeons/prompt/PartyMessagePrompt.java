package org.ninenetwork.infinitedungeons.prompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.remain.CompSound;
import org.ninenetwork.infinitedungeons.PlayerCache;

import javax.annotation.Nullable;

public class PartyMessagePrompt extends SimplePrompt {

	/*public ExpPrompt() {
		super(false);
	}*/

    @Override
    protected String getPrompt(ConversationContext context) {
        return "&6Write the message to set";
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        if (input.length() > 18)
            return false;

        return true;
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Message was too long, please try again.";
    }

    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
        Player player = this.getPlayer(context);
        PlayerCache cache = PlayerCache.from(player);
        cache.setGroupNote(input);
        CompSound.LEVEL_UP.play(player);

        tell("&6Set your group note to: &7" + cache.getGroupNote());
        return END_OF_CONVERSATION;
    }

}
