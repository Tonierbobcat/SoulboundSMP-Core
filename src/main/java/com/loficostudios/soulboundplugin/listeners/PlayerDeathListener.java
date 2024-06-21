/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.listeners;

import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.*;
import com.loficostudios.soulboundplugin.config.MainConfig;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import com.loficostudios.soulboundplugin.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Random;

public class PlayerDeathListener implements Listener {
    private final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    private final ProfileManager profileManager;

    public PlayerDeathListener(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    @SuppressWarnings("SpellCheckingInspection")
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Player killer = player.getKiller();

        //AtomicBoolean hasSouls = new AtomicBoolean(false);

        profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {

            List<SoulFragment> soulFragments = profile.getPlayersCurrentSoulFragments();

            boolean hasSouls;

            hasSouls = !soulFragments.isEmpty();

            if (hasSouls) {
                if (killer != null || MainConfig.DropLogic.DROP_WHEN_KILLED_BY_ANOTHER_PLAYER) {

                    player.sendMessage(SimpleColor.deserialize(MainConfig.Messages.DEATH_MESSAGE));

                    if (MainConfig.DropLogic.DROP_ALL) {
                        dropAllSoulFragments(profile, player, killer);
                    } else {
                        dropRandomSoulFragments(profile, player, killer);
                    }
                }
            }
        });

    }

    public void dropAllSoulFragments(Profile profile, Player player, Player killer) {
        for (SoulFragment soulFragment : profile.getPlayersCurrentSoulFragments()) {
            soulFragment.spawn(player.getLocation(), killer, player);

            profile.setBoolMap(soulFragment.getId(), true);
        }
    }

    public void dropRandomSoulFragments(Profile profile, Player player, Player killer) {
        Random rand = new Random();

        List<SoulFragment> soulFragments = profile.getPlayersCurrentSoulFragments();

        SoulFragment randomSoulFragment = soulFragments.get(rand.nextInt(soulFragments.size()));

        plugin.getFragmentManager().spawnSoulFragmentItem(randomSoulFragment.getId(), 1,

                player.getLocation(),
                killer,
                player);

        profile.setBoolMap(randomSoulFragment.getId(), true);
    }
}