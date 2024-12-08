package org.ninenetwork.infinitedungeons.dungeon;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.Map;

@Getter
public class DungeonRoomBloodRush extends DungeonRoom {

    private boolean opened;

    private boolean cleared;

    private int secretsTotal;

    private int secretsCleared;

    private Map<Entity,Boolean> completionMobsStatus;

    protected DungeonRoomBloodRush(String name) {
        super(name);
    }

    protected DungeonRoomBloodRush(String name, @Nullable DungeonRoomType type) {
        super(name, type);
    }

    @Override
    protected void onLoad() {
        this.opened = this.getBoolean("Opened", false);
        this.cleared = this.getBoolean("Cleared", false);
        this.secretsTotal = this.getInteger("Secrets_Amount", 1);
        this.secretsCleared = this.getInteger("Secrets_Cleared", 0);
        this.completionMobsStatus = this.getMap("Completion_Mobs", Entity.class, Boolean.class, this);

        super.onLoad();
    }

    @Override
    protected void onSave() {
        super.onSave();

        this.set("Opened", this.opened);
        this.set("Cleared", this.cleared);
        this.set("Secrets_Amount", this.secretsTotal);
        this.set("Secrets_Cleared", this.secretsCleared);
        this.set("Completion_Mobs", this.completionMobsStatus);
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        this.save();
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
        this.save();
    }

    public void setSecretsTotal(int secretsTotal) {
        this.secretsTotal = secretsTotal;
        this.save();
    }

    public void setSecretsCleared(int secretsCleared) {
        this.secretsCleared = secretsCleared;
        this.save();
    }

    public void setCompletionMobsStatus(Map<Entity, Boolean> completionMobsStatus) {
        this.completionMobsStatus = completionMobsStatus;
        this.save();
    }

}