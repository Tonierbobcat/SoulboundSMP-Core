/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.SoulboundSMPCore;
import com.loficostudios.soulboundplugin.managers.FragmentManager;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.Objects;


@CommandAlias("soulboundsmp|sbscore")
public class SoulboundSMPCommand extends BaseCommand {

	@Dependency
	private FragmentManager fragmentManager;

	@Dependency
	private ProfileManager profileManager;


	@Subcommand("version")
	public void onVersion(Player player) {
		player.sendMessage(SimpleColor.deserialize("&5SoulBoundSMPCore &71.0"));
		player.sendMessage(SimpleColor.deserialize("&fMade by: &eTonierbobcat&f@&bhttps://github.com/Tonierbobcat"));

	}

	@Subcommand("reload")
	public void onReload(Player player) throws IOException {
		SoulboundSMPCore.getInstance().getConfigFile().reload();
		SoulboundSMPCore.getInstance().getSoulFragmentSettings().reload();
		player.sendMessage(SimpleColor.deserialize("&bReloaded: &5SoulBoundSMPCore &71.0"));
	}

	@Subcommand("souls|s") @CommandPermission("sbscore.souls")
	public class soulsCommand extends BaseCommand {
		@Subcommand("list")
		public void onList(Player player) {
			player.sendMessage(SimpleColor.deserialize("&aAvailable Soul Fragments:"));
			fragmentManager.getSoulFragments().forEach(soulFragment -> {

				player.spigot().sendMessage(
						new ComponentBuilder(SimpleColor.deserialize("&f- &f" +  soulFragment.getDisplayName() + " &8ID: "  + soulFragment.getId()))
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/soulboundsmp souls add " + soulFragment.getId())).create());
			});
		}

		@Subcommand("add")
		@Syntax("<identifier>")
		@CommandCompletion("@soulfragments")
		public void onAdd(Player player, String identifier) {

			SoulFragment soulFragment = fragmentManager.getSoulFragmentById(identifier);

			profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
				profile.addSoulFragmentById(soulFragment.getId());



			});
		}

		@Subcommand("remove")
		@Syntax("<identifier>")
		@CommandCompletion("@soulfragments")
		public void onRemove(Player player, String identifier) {
			profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
				profile.removeSoulFragment(identifier);

				SoulFragment soulFragment = fragmentManager.getSoulFragmentById(identifier);

				boolean fragmentPositive = profile.getFragmentNegative(soulFragment.getId());

				String effectName = fragmentPositive ? soulFragment.getNegativeEffect() : soulFragment.getPositiveEffect();

				if (PotionEffectType.getByName(effectName.toUpperCase()) != null) {
					player.removePotionEffect(Objects.requireNonNull(PotionEffectType.getByName(effectName.toUpperCase())));
				}



			});
		}

		@Subcommand("resetnegative")
		@Syntax("<identifier>")
		public void onReset(Player player, String identifier) {
			profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
				fragmentManager.resetNegetiveSouls(player);
			});
		}
	}
}