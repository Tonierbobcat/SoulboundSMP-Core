/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.listeners;

import com.loficostudios.soulboundplugin.config.MainConfig;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Random;



public class MobDeathListener implements Listener {
    private final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (entity.getType().equals(EntityType.PLAYER)) return;
        if (killer == null || !killer.getType().equals(EntityType.PLAYER)) return;


        //determines whether mobs can drop souls.
        if (!MainConfig.MobLogic.DROPS_ENABLED) return;

        List<SoulFragment> soulFragments = plugin.getFragmentManager().getSoulFragments();

        Random rand = new Random();

        //defined in main config
        double probability = MainConfig.MobLogic.DROP_CHANCE;

        //If entity is slime it decreases probablity //set in config.
        if (MainConfig.MobLogic.SLIMES_DECREASE_PROBABILITY_ENABLED) {
            if (entity.getType().equals(EntityType.SLIME)) probability *= MainConfig.MobLogic.SLIME_DECREASE;
        }

        if (rand.nextDouble() < probability) dropSoulFragment(soulFragments, entity, killer);

    }

    private void dropSoulFragment(List<SoulFragment> soulFragments, LivingEntity entity, Player killer) {
        Random rand = new Random();

        SoulFragment randomSoulFragment = soulFragments.get(rand.nextInt(soulFragments.size()));

        randomSoulFragment.spawn(entity.getLocation(), killer, null);
        //plugin.getFragmentManager().spawnSoulFragItemMob(randomSoulFragment.getId(), entity.getLocation(), killer, null);
    }
}
