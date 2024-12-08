package org.ninenetwork.infinitedungeons.animation;

import de.slikey.effectlib.EffectManager;
import lombok.Getter;
import org.ninenetwork.infinitedungeons.InfiniteDungeonsPlugin;

public final class Effects {

    @Getter
    private static EffectManager effectManager;

    public static void load() {
        effectManager = new EffectManager(InfiniteDungeonsPlugin.getInstance());
    }

    public static void disable() {
        effectManager.dispose();
    }

}
