/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
*/

package com.loficostudios.soulboundplugin.listeners;

import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.melodyapi.utils.SimpleItem;
import com.loficostudios.soulboundplugin.managers.FragmentManager;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class AntiNegativePotion implements Listener {

private final FragmentManager fragmentManager;
private final ProfileManager profileManager;

    public AntiNegativePotion(FragmentManager fragmentManager, ProfileManager profileManager) {
        this.profileManager = profileManager;
        this.fragmentManager = fragmentManager;
    }



    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        NamespacedKey key = new NamespacedKey(SoulboundSMPCore.getInstance(), "myItems");

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String id = itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);

            // Check if the id matches the expected value
            if ("negativePotion".equals(id)) {
                // Perform actions accordingly
                // For example, removing the item and activating a potion
                player.getInventory().removeItem(itemStack);
                activatePotion(player);
            }
        }
    }


    private void activatePotion(Player player) {
        fragmentManager.resetNegetiveSouls(player);

        removePlayersNegativeEffects(player);

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_WITHER_DEATH,
                SoundCategory.PLAYERS,
                0.5f,
                1.5f
        );

        player.sendMessage(SimpleColor.deserialize("&aYou reset your negative souls!"));
    }

    private void removePlayersNegativeEffects(Player player) {
        profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
            //profile.addSoulFragmentById(id);
            //profile.

            for (SoulFragment soulFragment : profile.getPlayersCurrentSoulFragments()) {

                PotionEffectType effectType = PotionEffectType.getByName(soulFragment.getNegativeEffect());

                assert effectType != null;
                player.removePotionEffect(effectType);
            }

        });
    }

    public static void register() {

        ItemStack negativePotion = SimpleItem.createItem(SoulboundSMPCore.getInstance(), "negativePotion", Material.POTION, List.of(
                meta -> meta.setDisplayName(SimpleColor.deserialize("&fElixir of Soul R&#a8adffedemption")),
                meta -> meta.setCustomModelData(0),
                meta -> meta.addEnchant(Enchantment.DURABILITY, 1, false),
                meta -> meta.setLore(List.of(SimpleColor.deserialize("&9Clears negative soul effects")))
        ),List.of(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS), Color.fromRGB(233, 255, 69));

        ShapelessRecipe recipe0 = new ShapelessRecipe(
                new NamespacedKey(SoulboundSMPCore.getInstance(), "NegativePotionRecipe"),
                negativePotion
        );
        recipe0.addIngredient(3, Material.NETHER_STAR);

        //Add Shapeless
        Bukkit.addRecipe(recipe0);

        //Add Shaped
    }

}