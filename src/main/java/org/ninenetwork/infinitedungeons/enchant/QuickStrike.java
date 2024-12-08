package org.ninenetwork.infinitedungeons.enchant;

import lombok.Getter;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.enchant.SimpleEnchantment;
import org.mineacademy.fo.enchant.SimpleEnchantmentTarget;
import org.ninenetwork.infinitedungeons.util.GeneralUtils;

@AutoRegister(hideIncompatibilityWarnings = true)
public final class QuickStrike extends SimpleEnchantment {

    @Getter
    private static final QuickStrike instance = new QuickStrike();

    private QuickStrike() {
        super("Quick Strike", 4);
    }

    @Override
    public String getLore(int level) {
        if (level == 4) {
            return GeneralUtils.formatStringWithRainbowGradient("Quick Strike IV", false);
        } else {
            return "&1Quick Strike " + MathUtil.toRoman(level);
        }
    }

    @Override
    public SimpleEnchantmentTarget getTarget() {
        return SimpleEnchantmentTarget.WEAPON;
    }

}