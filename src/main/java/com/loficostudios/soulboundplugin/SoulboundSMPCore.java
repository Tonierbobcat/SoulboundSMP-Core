/**
 * @Author Tonierbobcat
 * @Github https://github.com/Tonierbobcat
 * @version SoulBoundSMPCore
 * @since 6/12/2024
 */

package com.loficostudios.soulboundplugin;

import co.aikar.commands.PaperCommandManager;
import com.loficostudios.melodyapi.utils.SimpleDocument;
import com.loficostudios.soulboundplugin.commands.SoulboundSMPCommand;
import com.loficostudios.soulboundplugin.fragment.SoulFragment;
import com.loficostudios.soulboundplugin.listeners.*;
import com.loficostudios.soulboundplugin.managers.FragmentManager;
import com.loficostudios.soulboundplugin.managers.ProfileManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
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

        PaperCommandManager manager = new PaperCommandManager(this);

        loadConfigFiles();

        manager.registerDependency(FragmentManager.class, fragmentManager);
        manager.registerDependency(ProfileManager.class, profileManager);

        manager.getCommandCompletions().registerAsyncCompletion("soulfragments", c -> {
            //return generatorManager.getGenerators().stream().map(generator -> generator.getGeneratorUUID().toString()).collect(Collectors.toList());
            return fragmentManager.getSoulFragments().stream().map(SoulFragment::getId).collect(Collectors.toList());
        });

        manager.registerCommand(new SoulboundSMPCommand());

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
        configFile = SimpleDocument.create(this,"config.yml", null);
        soulFragmentSettings = SimpleDocument.create(this,"soulfragments.yml", null);
        messageFile = SimpleDocument.create(this,"messages.yml", null);
    }

    private void initializeMelody() {
        if (getServer().getPluginManager().getPlugin("MelodyAPI") == null) {
            getLogger().severe(String.format("[%s] - Disabled due to MelodyAPI not found", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
    }



}