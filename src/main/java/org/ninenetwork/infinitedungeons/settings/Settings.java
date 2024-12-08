package org.ninenetwork.infinitedungeons.settings;

import org.mineacademy.fo.settings.Lang;
import org.mineacademy.fo.settings.SimpleSettings;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;

/**
 * A sample settings class, utilizing {@link YamlStaticConfig} with prebuilt settings.yml handler
 * with a bunch of preconfigured keys, see resources/settings.yml
 *
 * Foundation detects if you have "settings.yml" placed in your jar (in src/main/resources in source)
 * and will load this class automatically. The same goes for the {@link Lang} class which is
 * automatically loaded when we detect the presence of at least one localization/messages_X.yml
 * file in your jar.
 */
@SuppressWarnings("unused")
public final class Settings extends SimpleSettings {

    @Override
    protected int getConfigVersion() {
        return 1;
    }

    @Override
    protected boolean saveComments() {
        return false;
    }

    public static class PluginServerSettings {

        public static String DUNGEON_WORLD_NAME;
        public static Boolean DEBUG_MODE;

        /*
         * Automatically called method when we load settings.yml to load values in this subclass
         */
        private static void init() {
            setPathPrefix("Dungeon_Server-Wide_Settings");

            DUNGEON_WORLD_NAME = getString("Dungeon_World");
            DEBUG_MODE = getBoolean("Debug_Mode");
        }
    }

    public static class Items {

        public static String DAMAGE_STAT;
        public static String STRENGTH_STAT;
        public static String CRIT_DAMAGE_STAT;
        public static String CRIT_CHANCE_STAT;
        public static String HEALTH_STAT;
        public static String DEFENSE_STAT;
        public static String TRUE_DEFENSE_STAT;
        public static String INTELLIGENCE_STAT;
        public static String HEALTH_REGEN_STAT;
        public static String FEROCITY_STAT;
        public static String MANA_COST_STAT;
        public static String SPEED_STAT;
        public static String BONUS_ATTACK_STAT;
        public static String ABILITY_DAMAGE_STAT;
        public static String MAGIC_FIND_STAT;
        public static String VITALITY_STAT;

        public static String HYPERIONNAME;
        public static List<String> HYPERIONLORE;
        public static String STORMHELMETNAME;
        public static List<String> STORMHELMETLORE;

        /*
         * Automatically called method when we load settings.yml to load values in this subclass
         */
        private static void init() {
            setPathPrefix("Dungeon_Items");

            DAMAGE_STAT = getString("Damage_Stat_Format");
            STRENGTH_STAT = getString("Strength_Stat_Format");
            CRIT_DAMAGE_STAT = getString("Crit_Damage_Stat_Format");
            HEALTH_STAT = getString("Health_Stat_Format");
            DEFENSE_STAT = getString("Defense_Stat_Format");
            TRUE_DEFENSE_STAT = getString("True_Defense_Stat_Format");
            INTELLIGENCE_STAT = getString("Intelligence_Stat_Format");
            CRIT_CHANCE_STAT = getString("Crit_Chance_Stat_Format");
            HEALTH_REGEN_STAT = getString("Health_Regen_Stat_Format");
            FEROCITY_STAT = getString("Ferocity_Stat_Format");
            MANA_COST_STAT = getString("Mana_Cost_Stat_Format");
            SPEED_STAT = getString("Speed_Stat_Format");
            BONUS_ATTACK_STAT = getString("Bonus_Attack_Stat_Format");
            ABILITY_DAMAGE_STAT = getString("Ability_Damage_Stat_Format");
            MAGIC_FIND_STAT = getString("Magic_Find_Stat_Format");
            VITALITY_STAT = getString("Vitality_Stat_Format");

            HYPERIONNAME = getString("Hyperion_Name");
            HYPERIONLORE = getStringList("Hyperion_Lore");
            STORMHELMETNAME = getString("Storm_Helmet_Name");
            STORMHELMETLORE = getStringList("Storm_Helmet_Lore");
        }
    }

    public static class AutoMode {

        public static Boolean ENABLED;
        public static String RETURN_BACK_SERVER;

        /*
         * Automatically called method when we load settings.yml to load values in this subclass
         */
        private static void init() {
            setPathPrefix("Auto_Mode");

            ENABLED = getBoolean("Enabled");
            RETURN_BACK_SERVER = getString("Return_Back_Server");
        }
    }

    /*
     * Automatically called method when we load settings.yml to load values in this class
     *
     * See above for usage.
     */
    private static void init() {
        setPathPrefix(null);
    }

}