/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.fragment;

import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import com.loficostudios.soulboundplugin.config.MainConfig;
import com.loficostudios.soulboundplugin.utils.Metadata;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@EqualsAndHashCode
public class SoulFragment {

    @Getter private final String id;

    @Getter private final String displayName;

    @Getter private final Material material;
    @Getter private final int model;

    @Getter
    private final String positiveEffect;
    @Getter
    private final String negativeEffect;

    @Getter private final int maxStack;

    public SoulFragment(String id) {

        SoulboundSMPCore plugin = SoulboundSMPCore.getInstance();

        YamlDocument settings = plugin.getSoulFragmentSettings();

        this.id = id;

        this.displayName = SimpleColor.deserialize(settings.getString("soul-fragments." + id + ".name"));

        this.material = Enum.valueOf(Material.class, settings.getString("soul-fragments." + id + ".icon"));

        this.model = settings.getInt("soul-fragments." + id + ".custom-texture-id");

        this.positiveEffect = settings.getString("soul-fragments." + id + ".positive-effect");
        this.negativeEffect = settings.getString("soul-fragments." + id + ".negative-effect");

        this.maxStack = settings.getInt("soul-fragments." + id + ".max-stack");
    }

    //uses coin material and model to create a new itemstack
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null && model != 0) {
            meta.setCustomModelData(model);
            item.setItemMeta(meta);
        }

        return item;
    }

    //method to spawnSoulFragmentItem souls fragment
    public void spawn(Location location, Player killer, Player victim) {

        boolean dropNaturally = MainConfig.DropLogic.DROP_RANDOM_OFFSET;

        Item item = dropItem(location, toItemStack(), dropNaturally);
        if(item == null) return;

        Metadata.set(item, "soul_fragment_id", id);
        Metadata.set(item, "soul_killer", killer != null ? killer.getUniqueId().toString() : null);
        Metadata.set(item, "soul_victim", victim != null ? victim.getUniqueId().toString() : null);

    }


    private Item dropItem(Location location, ItemStack item, boolean naturally) {
        World world = location.getWorld();
        if (world == null) return null;

        return naturally ? location.getWorld().dropItemNaturally(location, item) : location.getWorld().dropItem(location, item);
    }

}
