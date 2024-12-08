package org.ninenetwork.infinitedungeons.command;

import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.SimpleLocalization;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReloadCommand extends SimpleCommand {

    public ReloadCommand() {
        super("dreload|drl");
        this.setDescription(SimpleLocalization.Commands.RELOAD_DESCRIPTION);
    }

    @Override
    protected void onCommand() {
        try {
            this.tell(new String[]{SimpleLocalization.Commands.RELOAD_STARTED});
            boolean syntaxParsed = true;
            List<File> yamlFiles = new ArrayList();
            this.collectYamlFiles(SimplePlugin.getData(), yamlFiles);
            Iterator var3 = yamlFiles.iterator();

            while(var3.hasNext()) {
                File file = (File)var3.next();

                try {
                    YamlConfig.fromFile(file);
                } catch (Throwable var6) {
                    var6.printStackTrace();
                    syntaxParsed = false;
                }
            }

            if (!syntaxParsed) {
                this.tell(new String[]{SimpleLocalization.Commands.RELOAD_FILE_LOAD_ERROR});
                return;
            }

            SimplePlugin.getInstance().reload();
            this.tell(new String[]{SimpleLocalization.Commands.RELOAD_SUCCESS});
        } catch (Throwable var7) {
            this.tell(new String[]{SimpleLocalization.Commands.RELOAD_FAIL.replace("{error}", var7.getMessage() != null ? var7.getMessage() : "unknown")});
            var7.printStackTrace();
        }

    }

    private List<File> collectYamlFiles(File directory, List<File> list) {
        if (directory.exists()) {
            File[] var3 = directory.listFiles();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.getName().endsWith("yml")) {
                    list.add(file);
                }

                if (file.isDirectory()) {
                    this.collectYamlFiles(file, list);
                }
            }
        }

        return list;
    }

    protected List<String> tabComplete() {
        return NO_COMPLETE;
    }

}