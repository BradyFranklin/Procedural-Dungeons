package org.ninenetwork.infinitedungeons.playerstats;

import org.ninenetwork.infinitedungeons.settings.Settings;

public enum PlayerStat {

    DAMAGE("Damage", Settings.Items.DAMAGE_STAT),
    STRENGTH("Strength", Settings.Items.STRENGTH_STAT),
    CRIT_DAMAGE("Crit_Damage", Settings.Items.CRIT_DAMAGE_STAT),
    CRIT_CHANCE("Crit_Chance", Settings.Items.CRIT_CHANCE_STAT),
    HEALTH("Health", Settings.Items.HEALTH_STAT),
    HEALTH_REGEN("Health_Regen", Settings.Items.HEALTH_REGEN_STAT),
    DEFENSE("Defense", Settings.Items.DEFENSE_STAT),
    TRUE_DEFENSE("True_Defense", Settings.Items.TRUE_DEFENSE_STAT),
    INTELLIGENCE("Intelligence", Settings.Items.INTELLIGENCE_STAT),
    FEROCITY("Ferocity", Settings.Items.FEROCITY_STAT),
    MANACOST("Mana_Cost", Settings.Items.MANA_COST_STAT),
    SPEED("Speed", Settings.Items.SPEED_STAT),
    BONUS_ATTACK("Attack_Speed", Settings.Items.BONUS_ATTACK_STAT),
    ABILITY_DAMAGE("Ability_Damage", Settings.Items.ABILITY_DAMAGE_STAT),
    MAGIC_FIND("Magic_Find", Settings.Items.MAGIC_FIND_STAT),
    VITALITY("Vitality", Settings.Items.VITALITY_STAT);


    public final String label;
    public final String statFormat;

    PlayerStat(String label, String statFormat) {
        this.label = label;
        this.statFormat = statFormat;
    }

}
