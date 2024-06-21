/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.utils;

import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import lombok.experimental.UtilityClass;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.Optional;

@UtilityClass
public class Metadata {
    private static final SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

    public Optional<Object> get(Metadatable object, String key) {
        for (MetadataValue value : object.getMetadata(key)) {
            if (value.getOwningPlugin() != plugin) continue;
            return Optional.ofNullable(value.value());
        }

        return Optional.empty();
    }

    public boolean has(Metadatable object, String key) {
        return get(object, key).isPresent();
    }

    public void set(Metadatable object, String key, Object value) {
        object.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
}
