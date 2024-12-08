package org.ninenetwork.infinitedungeons.menu;

import org.mineacademy.fo.menu.MenuTools;
import org.ninenetwork.infinitedungeons.item.tool.EssenceSecretTool;
import org.ninenetwork.infinitedungeons.item.tool.MobSpawnpointTool;
import org.ninenetwork.infinitedungeons.item.tool.RegionTool;
import org.ninenetwork.infinitedungeons.item.tool.BlessingSecretTool;

public class ToolsMenu extends MenuTools {

    public ToolsMenu() {
        this.setTitle("Tools Menu");
    }

    @Override
    protected Object[] compileTools() {
        return new Object[]{
                0, EssenceSecretTool.class, BlessingSecretTool.class, RegionTool.class, MobSpawnpointTool.class
        };
    }

    @Override
    protected String[] getInfo() {
        return new String[]{
                "Click a tool to get it!"
        };
    }
}