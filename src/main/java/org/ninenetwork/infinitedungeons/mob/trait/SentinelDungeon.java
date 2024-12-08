package org.ninenetwork.infinitedungeons.mob.trait;

import org.bukkit.entity.LivingEntity;
import org.mcmonkey.sentinel.SentinelIntegration;

public class InfiniteSentinelTrait extends SentinelIntegration {

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
        return super.isTarget(ent, prefix, value);
    }

}