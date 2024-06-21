/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.listeners;


import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.config.MainConfig;
import com.loficostudios.soulboundplugin.managers.FragmentManager;
import com.loficostudios.soulboundplugin.utils.Metadata;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.UUID;

public class CollectListener implements Listener {
    private final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    private final ProfileManager profileManager;
    private final FragmentManager fragmentManager;

    public CollectListener(ProfileManager profileManager, FragmentManager fragmentManager) {
        this.profileManager = profileManager;
        this.fragmentManager = fragmentManager;
    }

    @EventHandler
    public void onCollect(EntityPickupItemEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;

        Player player = (Player) event.getEntity();
        Item item = event.getItem();

        String type = Metadata.get(item, "soul_fragment_id").map(Object::toString).orElse(null);
        if (type == null) return;

        event.setCancelled(true);

        UUID killer = Metadata.get(item, "soul_killer").map(Object::toString).map(UUID::fromString).orElse(null);
        UUID victim = Metadata.get(item, "soul_victim").map(Object::toString).map(UUID::fromString).orElse(null);

        if (!canPickUp(player, killer, victim)) return;

        Collect(player, type, item);

    }

    private boolean canPickUp(Player player, UUID killer, UUID victim) {
        boolean isKiller;

        if (killer != null) isKiller = player.getUniqueId().equals(killer);

        boolean isVictim = victim != null && player.getUniqueId().equals(victim);

        if (MainConfig.DropLogic.PICKUP_OWN) return true;

        return !isVictim;
    }

    void Collect(Player player, String type, Item item) {
        if (!soulFragmentSlotFull(player, type) || isSoulFragmentNegetive(player, type) && MainConfig.GeneralSection.SOUL_PICKUP_CONVERSION) {
            pickup(player, type, item);
        } else if (!isSoulFragmentNegetive(player, type)) {
            pickupFull(player, type, item);
        }
    }

    private void pickupFull(Player player, String id, Item item) {
        item.remove();

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_ENDER_EYE_DEATH,
                SoundCategory.RECORDS,
                1f,
                0.5f
        );

        player.sendMessage(SimpleColor.deserialize(MainConfig.Messages.SOUL_SLOT_FULL)
                .replace("<soul>", fragmentManager.getSoulFragmentById(id).getDisplayName()));
    }

    private void pickup(Player player, String id, Item item) {
        item.remove();

        profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
            profile.addSoulFragmentById(id);
        });

        if (MainConfig.GeneralSection.PICKUP_SOUND_ENABLED) {
            player.playSound(
                player.getLocation(),
                Sound.valueOf(MainConfig.GeneralSection.SOUND_TYPE),
                SoundCategory.RECORDS,
                MainConfig.GeneralSection.SOUND_VOLUME.floatValue(),
                MainConfig.GeneralSection.SOUND_PITCH.floatValue()
            );
        }
    }


    private boolean soulFragmentSlotFull(Player player, String type) {
        boolean[] soulFragmentCountExceedsMax = { false };

        profileManager.getProfile(player.getUniqueId()).ifPresent( profile -> soulFragmentCountExceedsMax[0] = profile.getSoulFragmentCount(type) >= fragmentManager.getSoulFragmentById(type).getMaxStack());

        return soulFragmentCountExceedsMax[0];
    }

    private boolean isSoulFragmentNegetive(Player player, String type) {
        boolean[] isSoulFragmentNegetive = { false };

        profileManager.getProfile(player.getUniqueId()).ifPresent( profile ->  {

            isSoulFragmentNegetive[0] = profile.getFragmentNegative(type);
        });

        return isSoulFragmentNegetive[0];
    }
}
