package org.ninenetwork.infinitedungeons.command;

import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.SimpleLocalization;

public class ReloadCommand extends SimpleCommand {

    public ReloadCommand() {
        super("dreload");
    }

    @Override
    protected void onCommand() {
        try {
            SimplePlugin.getInstance().reload();
            tell(SimpleLocalization.Commands.RELOAD_SUCCESS);
        } catch (Throwable t) {
            t.printStackTrace();
            tell(SimpleLocalization.Commands.RELOAD_FAIL.replace("(error)",t.toString()));
        }
    }

}