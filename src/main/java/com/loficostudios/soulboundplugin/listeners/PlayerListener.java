/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.listeners;

import com.loficostudios.soulboundplugin.exceptions.ProfileAlreadyLoadedException;
import com.loficostudios.soulboundplugin.exceptions.ProfileNotLoadedException;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ProfileManager profileManager;

    public PlayerListener(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    private void handleJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        try {
            profileManager.loadProfile(player.getUniqueId());
        } catch (ProfileAlreadyLoadedException e) {
            player.sendMessage(("&cYour profile is already loaded."));
        }
    }

    @EventHandler
    private void handleQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        try {
            profileManager.unloadProfile(player.getUniqueId());
        } catch (ProfileNotLoadedException e) {
            Bukkit.getLogger().warning("Player " + player.getName() + " tried to unload his profile but it was not loaded.");
        }
    }
}
