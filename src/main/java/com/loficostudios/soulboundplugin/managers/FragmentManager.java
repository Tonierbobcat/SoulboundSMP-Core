/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.managers;

import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.config.MainConfig;
import com.loficostudios.soulboundplugin.utils.Metadata;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


public class FragmentManager {
    private static final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    private final ProfileManager profileManager;

    public FragmentManager(final ProfileManager profileManager)  {
        this.profileManager = profileManager;
    }

    public List<SoulFragment> getSoulFragments() {
        return SoulboundSMPCore.getInstance().getSoulFragmentSettings()
            .getSection("soul-fragments")
            .getRoutesAsStrings(false).stream()
                .map(SoulFragment::new).toList();
    }

    public void spawnSoulFragmentItem(String fragmentId, int amountThatDropsFromPlayer, Location location, Player killer, Player victim) {
        SoulboundSMPCore.getInstance().getServer().broadcastMessage(SimpleColor.deserialize("&cString &#a8ff9eMy name is bob"));

        for (int i = 0; i < amountThatDropsFromPlayer; i++) {
            SoulFragment fragment = getSoulFragmentById(fragmentId);
            fragment.spawn(location, killer, victim);
        }
    }

    public SoulFragment getSoulFragmentById(String fragmentId) {
        //SoulboundSMPCore.getInstance().getServer().broadcastMessage("soul fragment after triggering getsoul =" + fragmentId);

        for (SoulFragment soulFragment : getSoulFragments()) {
          if (soulFragment.getId().equals(fragmentId)) {
             return soulFragment;
          }
        }

        //SoulboundSMPCore.getInstance().getServer().broadcastMessage("soul fragment id after passing to getSoulFrag is =" + fragmentId);
        return null; // Return null if the soul fragment with the given ID is not found
    }

    public void DestroyAllSoulFragmentGroundItems() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                //PersistentDataContainer pdc = entity.getPersistentDataContainer();
                if (Metadata.has(entity, "soul_fragment_id")) {
                    entity.remove();
                }
            }
        }
    }


    public void resetNegetiveSouls(Player player) {
        profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {


            for (SoulFragment soulFragment : profile.getPlayersCurrentSoulFragments()) {

                profile.setBoolMap(soulFragment.getId(), false);
            }

        });
    }

    public void startFragmentEffectsTask() {
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (player.isDead()) {
                        continue;
                    }

                    profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {

                        for (SoulFragment soulFragment : profile.getPlayersCurrentSoulFragments()) {
                            String positiveEffect = soulFragment.getPositiveEffect();
                            String negativeEffect = soulFragment.getNegativeEffect();

                            PotionEffectType effectType;

                            int potionAplifier = profile.getSoulFragmentCount(soulFragment.getId());

                            boolean isSoulNegetive = profile.getFragmentNegative(soulFragment.getId());

                            if (!isSoulNegetive)
                                effectType = PotionEffectType.getByName(positiveEffect.toUpperCase());
                            else
                                effectType = PotionEffectType.getByName(negativeEffect.toUpperCase());

                            handleEffects(
                                    player,
                                    effectType,
                                    potionAplifier  - 1,
                                    !MainConfig.GeneralSection.HIDE_EFFECT_PARTICLES
                            );


                            //handleEffects(player, getPotionEffectType(negativeEffect), potionAplifier, icon);
                        }

                    });
                }
            }
        }.runTaskTimer(SoulboundSMPCore.getInstance(), 0L, 5L);
    }

    //THIS IS USED FOR SPAWN PARTICLES ON SOULS

    public void spawnParticlesAroundEntities() {
        // Get all entities currently loaded in the world
        List<Entity> entities = Bukkit.getWorlds().get(0).getEntities();

        // Iterate through each entity
        for (Entity entity : entities) {
            // Check if the entity is alive (not dead)
            if (entity instanceof Item item && Metadata.has(item, "soul_fragment_id")) {

                //String type = Metadata.get(item, "soul_fragment_id").map(Object::toString).orElse(null);

                Particle particle = Particle.SPELL_MOB;

                int amount = 2;

                // Spawn particles around the item
                item.getWorld().spawnParticle(particle, entity.getLocation().add(0, 0.2, 0), amount);
            }
        }
    }

    private void handleEffects(Player player, PotionEffectType effectType, int amplifier, boolean particles) {
        PotionEffect effect = new PotionEffect(
                effectType,
                20 * 11,
                amplifier,
                false,
                particles,
                false
        );

        player.addPotionEffect(effect);
    }
}
