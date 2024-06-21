/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.config;

import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import dev.dejvokep.boostedyaml.YamlDocument;

public class MainConfig {

    private static final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    private static final YamlDocument config = plugin.getConfigFile();

    private static final YamlDocument messages = plugin.getMessageFile();

    public static class GeneralSection {
        public static boolean SOUL_PICKUP_CONVERSION = config.getBoolean("soul-pickup-conversion");
        public static boolean PICKUP_SOUND_ENABLED = config.getBoolean("play-pickup-sound.enabled");
        public static String SOUND_TYPE = config.getString("play-pickup-sound.sound");
        public static Double SOUND_VOLUME = config.getDouble("play-pickup-sound.volume");
        public static Double SOUND_PITCH = config.getDouble("play-pickup-sound.pitch");

        //public static boolean HIDE_EFFECT_ICON = config.getBoolean("hide-effect-icon");

        public static boolean HIDE_EFFECT_PARTICLES = config.getBoolean("hide-effect-particles");
    }

    public static  class DropLogic {

        public static boolean PICKUP_OWN = config.getBoolean("drop-logic.pickup-own");
        public static boolean DROP_WHEN_KILLED_BY_ANOTHER_PLAYER = config.getBoolean("drop-logic.drop-when-killed");
        public static boolean DROP_ALL = config.getBoolean("drop-logic.drop-all-soul-fragments");
        public static boolean DROP_RANDOM_OFFSET = config.getBoolean("drop-logic.drop-random-offset");

    }

    public static class MobLogic {
        public static boolean DROPS_ENABLED = config.getBoolean("mobs.mobs-drop-souls");

        public static Double DROP_CHANCE =  config.getDouble("mobs.drop-chance");

        public static boolean SLIMES_DECREASE_PROBABILITY_ENABLED = config.getBoolean("mobs.slimes-decrease-probablity");

        public static Double SLIME_DECREASE =  config.getDouble("mobs.slime-probablity-decrease");
    }

    public static class Messages {
        public static String SOUL_PICKUP = messages.getString("soul-pickup");
        public static String SOUL_PICKUP_NEGATIVE = messages.getString("soul-pickup-while-negative");
        public static String SOUL_SLOT_FULL = messages.getString("soul-slot-full");
        public static String CONVERTED_TO_POSITIVE_SOUL = messages.getString("converted-to-positive-soul");
        public static String DEATH_MESSAGE = messages.getString("death-message");
    }

}
