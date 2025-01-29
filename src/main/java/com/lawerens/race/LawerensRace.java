package com.lawerens.race;

import com.lawerens.race.model.RaceConfig;
import com.lawerens.race.model.RaceInfo;
import com.lawerens.race.model.Rollback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;

public final class LawerensRace extends JavaPlugin {

    private static LawerensRace INSTANCE;
    private RaceInfo raceInfo;
    private final Map<UUID, Rollback> rollbacks = new HashMap<>();
    private RaceConfig raceConfig;
    private GameManager gameManager;

    public static LawerensRace get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fHabilitando plugin...");

        INSTANCE = this;

        raceInfo = new RaceInfo();
        raceConfig = new RaceConfig(this);
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando configuración...");

        raceConfig.registerConfig();
        raceConfig.load();

        getCommand("lrsetup").setExecutor(new EventSetupCommand());
        getCommand("evento").setExecutor(new EventCommand());

        getServer().getPluginManager().registerEvents(new PreGameListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        gameManager = new GameManager();
    }

    public static boolean checkPlayer(Player player){
        return get().getGameManager().getPlayers().contains(player);
    }

    @Override
    public void onDisable() {
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fDeshabilitando plugin...");

        if(getGameManager().getTaskID() != -1) Bukkit.getScheduler().cancelTask(getGameManager().getTaskID());
        rollbacks.forEach((uuid, rollback) -> {
            if(Bukkit.getPlayer(uuid) != null){
                rollback.give(true);
                rollbacks.remove(uuid);

            }
        });
    }
}
