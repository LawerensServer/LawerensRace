package com.lawerens.race;

import org.bukkit.plugin.java.JavaPlugin;

public final class LawerensRace extends JavaPlugin {

    private static LawerensRace INSTANCE;
    private ParkourInfo parkourInfo;
    private final Map<UUID, Rollback> rollbacks = new HashMap<>();
    private ParkourConfig parkourConfig;
    private GameManager gameManager;

    public static LawerensRace get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fHabilitando plugin...");

        INSTANCE = this;

        parkourInfo = new ParkourInfo();
        parkourConfig = new ParkourConfig(this);
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando configuración...");

        parkourConfig.registerConfig();
        parkourConfig.load();

        getCommand("lesetup").setExecutor(new EventSetupCommand());
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
