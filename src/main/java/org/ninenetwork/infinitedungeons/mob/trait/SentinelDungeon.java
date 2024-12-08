package org.ninenetwork.infinitedungeons.mob.trait;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelIntegration;

public class SentinelDungeon extends SentinelIntegration {

    public SentinelDungeon() {

    }

    @Override
    public String getTargetHelp() {
        return "";
    }

    @Override
    public String[] getTargetPrefixes() {
        return new String[0];
    }

    @Override
    public boolean isTarget(LivingEntity ent, String prefix, String value) {
        try {
            if (ent instanceof Player) {
                return true;
            }
        }
        catch (IllegalArgumentException ex) {
            // TODO: Maybe show a one-time warning?
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /*
    @Override
    public boolean tryAttack(SentinelTrait st, LivingEntity ent) {
        return super.tryAttack(st, ent);
    }
    */
}