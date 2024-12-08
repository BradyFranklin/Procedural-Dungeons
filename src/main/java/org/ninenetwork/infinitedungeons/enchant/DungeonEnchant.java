package org.ninenetwork.infinitedungeons.item;

import org.ninenetwork.infinitedungeons.util.GeneralUtils;

public enum DungeonEnchant {

    QUICK_STRIKE1("QuickStrike1", "&9Quick Strike I"),
    QUICK_STRIKE2("QuickStrike2", "&9Quick Strike II"),
    QUICK_STRIKE3("QuickStrike3", "&9Quick Strike III"),
    QUICK_STRIKE4("QuickStrike4", GeneralUtils.formatStringWithRainbowGradient("Quick Strike IV", false)),
    REPLENISH1("Replenish1", "&9Replenish I"),
    REPLENISH2("Replenish2", "&9Replenish II"),
    REPLENISH3("Replenish3", "&9Replenish III"),
    REPLENISH4("Replenish4", "&9Replenish IV"),
    REPLENISH5("Replenish5", GeneralUtils.formatStringWithRainbowGradient("Replenish V", false)),
    GIANT_KILLER1("GiantKiller1", "&9Giant Killer I"),
    SOUL_EATER1("SoulEater1", "&9Soul Eater I"),
    OVERLOAD1("Overload1", "&9Overload I");

    public final String label;

    public final String lore;

    private DungeonEnchant(String label, String lore) {
        this.label = label;
        this.lore = lore;
    }

    public static DungeonEnchant findEnchantByLabel(String label) {
        for (DungeonEnchant enchant : DungeonEnchant.values()) {
            if (enchant.label.equals(label)) {
                return enchant;
            }
        }
        return null;
    }

}
