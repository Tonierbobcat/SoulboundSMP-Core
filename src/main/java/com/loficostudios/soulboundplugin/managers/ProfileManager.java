/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin.managers;


import com.loficostudios.soulboundplugin.profile.Profile;
import com.loficostudios.soulboundplugin.exceptions.ProfileAlreadyLoadedException;
import com.loficostudios.soulboundplugin.exceptions.ProfileNotLoadedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProfileManager {

	private final Map<UUID, Profile> profiles = new HashMap<>();

	public void loadProfile(UUID uuid) throws ProfileAlreadyLoadedException {
		if(profiles.containsKey(uuid)) {
			throw new ProfileAlreadyLoadedException("Profile with uuid " + uuid + " is already loaded");
		}

		profiles.put(uuid, new Profile(uuid));
	}

	public void unloadProfile(UUID uuid) throws ProfileNotLoadedException {
		if(!profiles.containsKey(uuid)) {
			throw new ProfileNotLoadedException("Profile with uuid " + uuid + " is not loaded");
		}
		profiles.remove(uuid);
	}

	public Optional<Profile> getProfile(UUID uuid) {
		return Optional.ofNullable(profiles.get(uuid));
	}

	public boolean isProfileLoaded(UUID uuid) {
		return profiles.containsKey(uuid);
	}

}
