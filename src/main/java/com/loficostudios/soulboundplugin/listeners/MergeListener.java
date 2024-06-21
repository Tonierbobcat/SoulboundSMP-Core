/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.listeners;

import com.loficostudios.soulboundplugin.utils.Metadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;

public class MergeListener implements Listener {

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        if (!Metadata.has(event.getEntity(), "soul_fragment_id")) return;
        event.setCancelled(true);
    }
}
