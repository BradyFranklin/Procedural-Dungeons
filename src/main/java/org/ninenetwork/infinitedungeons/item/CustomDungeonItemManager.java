package org.ninenetwork.infinitedungeons.item.weapons;

import lombok.Getter;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;

import java.util.HashSet;
import java.util.Set;

public class CustomDungeonItemManager {

    private final InfiniteDungeonsPlugin plugin;
    private final Set<AbstractDungeonItem> itemRegistry = new HashSet<>();

    @Getter
    private static final CustomDungeonItemManager instance = new CustomDungeonItemManager(InfiniteDungeonsPlugin.getInstance());

    public CustomDungeonItemManager(InfiniteDungeonsPlugin plugin) {
        this.plugin = plugin;

        registerHandler(new Hyperion(plugin));
    }

    public void registerHandler(AbstractDungeonItem... handlers) {
        for (AbstractDungeonItem handler : handlers) {
            this.registerHandler(handler);
        }
    }

    public void registerHandler(AbstractDungeonItem handler) {
        itemRegistry.add(handler);
        //plugin.registerListener(handler);
    }

    public <T> T getHandler(Class<? extends T> clazz) {
        for (AbstractDungeonItem handler : itemRegistry) {
            if (handler.getClass().equals(clazz)) {
                return (T) handler;
            }
        }
        return null;
    }

    public AbstractDungeonItem getHandler(String itemId) {
        for (AbstractDungeonItem handler : itemRegistry) {
            if (handler.itemID.equals(itemId)) {
                return handler;
            }
        }
        return null;

    }

}