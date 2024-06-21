/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin;

import com.loficostudios.melodyapi.libs.boostedyaml.YamlDocument;
import com.loficostudios.melodyapi.utils.SimpleColor;
import com.loficostudios.melodyapi.utils.SimpleDocument;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.listeners.*;
import com.loficostudios.soulboundplugin.managers.FragmentManager;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;



import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SoulboundSMPCore extends JavaPlugin {

    @Getter
    private static SoulboundSMPCore instance;

    @Getter private YamlDocument configFile;
    @Getter private YamlDocument soulFragmentSettings;
    @Getter private YamlDocument messageFile;

    @Getter
    private final ProfileManager profileManager = new ProfileManager();
    @Getter
    private final FragmentManager fragmentManager = new FragmentManager(profileManager);



    @Override
    public void onEnable() {
        instance = this;

        initializeMelody();

        //PaperCommandManager manager = new PaperCommandManager(this);

        loadConfigFiles();

        //manager.registerDependency(FragmentManager.class, fragmentManager);
        //manager.registerDependency(ProfileManager.class, profileManager);

        //

        //manager.getCommandCompletions().registerAsyncCompletion("soulfragments", c -> {
        //    return fragmentManager.getSoulFragments().stream().map(SoulFragment::getId).collect(Collectors.toList());
        //});

        //manager.registerCommand(new SoulboundSMPCommand());

        new CommandAPICommand("addsoul")
                /*.withArguments(new ListArgumentBuilder<String>("souls")
                        .allowDuplicates(false)
                        .withList(fragmentManager.getSoulFragments().stream().map(SoulFragment::getId).collect(Collectors.toList()))
                        .withStringMapper().buildGreedy())*/
                .withArguments(new StringArgument("soul")
                        .replaceSuggestions(ArgumentSuggestions.strings(info -> fragmentManager.getSoulFragments().stream().map(SoulFragment::getId).toArray(String[]::new))))
                .executesPlayer((player, args) -> {
                    SoulFragment soulFragment = fragmentManager.getSoulFragmentById(Objects.requireNonNull(args.get("soul")).toString());

                    if (soulFragment != null) {
                        profileManager.getProfile(player.getUniqueId()).ifPresent(profile -> {
                            profile.addSoulFragmentById(soulFragment.getId());
                        });
                    } else {
                        player.sendMessage(SimpleColor.deserialize("&cInvalid soul ID"));
                    }
                })
                .register();

        new CommandAPICommand("soullist")
                .executesPlayer((player, args) -> {
                    player.sendMessage(SimpleColor.deserialize("&aAvailable Soul Fragments:"));
                    fragmentManager.getSoulFragments().forEach(soulFragment -> {

                        player.spigot().sendMessage(
                                new ComponentBuilder(SimpleColor.deserialize("&f- &f" +  soulFragment.getDisplayName() + " &8ID: "  + soulFragment.getId()))
                                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/addsoul " + soulFragment.getId())).create());
                    });
                })
                .register();

        AntiNegativePotion.register();

        fragmentManager.startFragmentEffectsTask();

        new BukkitRunnable() {
            @Override
            public void run() {
                fragmentManager.spawnParticlesAroundEntities();
            }
        }.runTaskTimer(this, 0, 10); // Run every second (20 ticks)*/

        registerEvents();

    }

    @Override
    public void onDisable() {
        fragmentManager.DestroyAllSoulFragmentGroundItems();
    }

    private void registerEvents() {
        Arrays.asList(
                new MobDeathListener(),
                new CollectListener(profileManager, fragmentManager),
                new PlayerListener(profileManager),
                new PlayerDeathListener(profileManager),
                new MergeListener(),
                new AntiNegativePotion(fragmentManager, profileManager)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void loadConfigFiles() {
        //When create a config files. when you are loading it directly from resources you can leave file location null
        configFile = SimpleDocument.create(this,"config.yml");
        soulFragmentSettings = SimpleDocument.create(this,"soulfragments.yml");
        messageFile = SimpleDocument.create(this,"messages.yml");
    }

    private void initializeMelody() {
        if (getServer().getPluginManager().getPlugin("MelodyAPI") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to MelodyAPI not found", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }
}
