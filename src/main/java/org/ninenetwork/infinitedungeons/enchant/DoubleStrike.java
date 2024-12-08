package org.ninenetwork.infinitedungeons.enchant;

import lombok.Getter;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.enchant.SimpleEnchantment;
import org.mineacademy.fo.enchant.SimpleEnchantmentTarget;

@AutoRegister(hideIncompatibilityWarnings = true)
public final class DoubleStrike extends SimpleEnchantment {

    @Getter
    private static final DoubleStrike instance = new DoubleStrike();

    private DoubleStrike() {
        super("Double Strike", 4);
    }

    @Override
    public SimpleEnchantmentTarget getTarget() {
        return SimpleEnchantmentTarget.WEAPON;
    }

}