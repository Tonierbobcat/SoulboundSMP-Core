/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.profile;

import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import com.loficostudios.soulboundplugin.config.MainConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Profile {

	@Getter
	private final UUID uuid;


	//Creates a way to store a soulfragment and the amounts
	private final Map<SoulFragment, Integer> soulFragments;
	private final Map<String, Boolean> soulFragmentBooleanMap;

	//@Getter private final int maxSoulFragmentStack = MainConfig.GeneralSection.MAX_SOUL_STACK;

    public Profile(UUID uuid) {
		this.uuid = uuid;
		this.soulFragments = new HashMap<>();
		this.soulFragmentBooleanMap = new HashMap<>();
	}

	public void addSoulFragmentById(String fragmentId) {
		SoulFragment soulFragment =  SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId);
		if (soulFragment != null) {


			Integer count = soulFragments.get(soulFragment);

			if (count == null) count = 0;

			boolean isSoulNegative = getFragmentNegative(soulFragment.getId());

			Player player = Bukkit.getPlayer(uuid);

			if (count < soulFragment.getMaxStack() && !isSoulNegative) {

                assert player != null;

				//pickup message
                player.sendMessage(SimpleColor.deserialize(MainConfig.Messages.SOUL_PICKUP)
						.replace("<soul>", SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId).getDisplayName()));
				soulFragments.put(soulFragment, count + 1);
				soulFragmentBooleanMap.put(fragmentId, false);
			}

			//what happens when players gets to max souls


			//true is placeholder for config
			// converts players souls to positive once negative souls are at 0
			if (isSoulNegative && MainConfig.GeneralSection.SOUL_PICKUP_CONVERSION) {
				if (count != 1) {

					assert player != null;

					//pickup message while negative
					player.sendMessage(SimpleColor.deserialize(MainConfig.Messages.SOUL_PICKUP_NEGATIVE)
							.replace("<soul>", SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId).getDisplayName()));

					//replace with SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId).getDisplayName()

					player.removePotionEffect(Objects.requireNonNull(PotionEffectType.getByName(soulFragment.getNegativeEffect().toUpperCase())));
					soulFragments.put(soulFragment, count - 1);
				}

				else if (count == 1) {
					setBoolMap(fragmentId, false);

                    assert player != null;
					//converted message
                    player.sendMessage(SimpleColor.deserialize(MainConfig.Messages.CONVERTED_TO_POSITIVE_SOUL)
							.replace("<soul>", SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId).getDisplayName()));

                    player.removePotionEffect(Objects.requireNonNull(PotionEffectType.getByName(soulFragment.getNegativeEffect().toUpperCase())));
				}
			}
		}
	}

	public void removeSoulFragment(String fragmentId) {
		SoulFragment soulFragment =  SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId);
		if (soulFragment == null)
			return;

		Player player = Bukkit.getPlayer(uuid);

		int count = soulFragments.getOrDefault(soulFragment, 0);

		if (count > 0) {
			soulFragments.put(soulFragment, count - 1);
			if (count == 1) { // Check if count becomes zero after decrement
				soulFragments.remove(soulFragment);
			}
		}
	}

	public boolean hasSoulFragment(String fragmentId, int amount) {
		SoulFragment soulFragment =  SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId);
		return soulFragments.getOrDefault(soulFragment, 0) >= amount;
	}

	public int getSoulFragmentCount(String fragmentId) {
		SoulFragment soulFragment = SoulboundSMPCore.getInstance().getFragmentManager().getSoulFragmentById(fragmentId);

		if (soulFragment != null) {
			return soulFragments.getOrDefault(soulFragment, 0);
		} else {
			SoulboundSMPCore.getInstance().getServer().broadcastMessage("Soul Fragment: " + fragmentId + " Cannot be found");
			return 0;
		}
	}

	// Method to convert the map into a list of SoulFragment
	public List<SoulFragment> getPlayersCurrentSoulFragments() {

        return new ArrayList<>(soulFragments.keySet());
	}



	public boolean getFragmentNegative(String fragmentId) {
		return soulFragmentBooleanMap.getOrDefault(fragmentId, false);
	}

	public void setBoolMap(String fragmentId, boolean value) {
		soulFragmentBooleanMap.put(fragmentId, value);
	}


}
