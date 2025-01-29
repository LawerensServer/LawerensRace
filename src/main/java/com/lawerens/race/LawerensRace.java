package com.lawerens.race;

import com.lawerens.events.LawerensEvent;
import com.lawerens.events.LawerensEvents;
import com.lawerens.race.commands.EventSetupCommand;
import com.lawerens.race.listeners.GameListener;
import com.lawerens.race.listeners.PreGameListener;
import com.lawerens.race.listeners.QuitListener;
import com.lawerens.race.model.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;
import static xyz.lawerens.utils.LawerensUtils.*;
import static xyz.lawerens.utils.LawerensUtils.sendUnderline;

@Getter
public final class LawerensRace extends JavaPlugin implements LawerensEvent {

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
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "CARRERA EVENTO", "&fHabilitando plugin...");

        INSTANCE = this;

        raceInfo = new RaceInfo();
        raceConfig = new RaceConfig(this);
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "CARRERA EVENTO", "&fCargando configuración...");

        raceConfig.registerConfig();
        raceConfig.load();

        getCommand("lcsetup").setExecutor(new EventSetupCommand());

        getServer().getPluginManager().registerEvents(new PreGameListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        gameManager = new GameManager();

        // LawerensEvents.getPlugin(LawerensEvents.class).registerEvent(this);
    }

    public static boolean checkPlayer(Player player){
        return get().getGameManager().getPlayers().contains(player);
    }

    @Override
    public void onDisable() {
        sendMessageWithPrefix(Bukkit.getConsoleSender(), "CARRERA EVENTO", "&fDeshabilitando plugin...");

        if(getGameManager().getTaskID() != -1) Bukkit.getScheduler().cancelTask(getGameManager().getTaskID());
        rollbacks.forEach((uuid, rollback) -> {
            if(Bukkit.getPlayer(uuid) != null){
                rollback.give(true);
                rollbacks.remove(uuid);

            }
        });
    }

    @Override
    public String getEventName() {
        return "Parkour";
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this;
    }

    @Override
    public void start() {
        if(LawerensRace.get().getRaceInfo().getFinishCuboid() == null || LawerensRace.get().getRaceInfo().getStartLocation() == null || LawerensRace.get().getRaceInfo().getLobbyLocation() == null){
            return;
        }

        if(LawerensRace.get().getGameManager().getState() != RaceState.WAITING || LawerensRace.get().getGameManager().isEnable()){
            return;
        }
        LawerensRace.get().getGameManager().setEnable(true);
    }

    @Override
    public void finish() {
        if(!LawerensRace.get().getGameManager().isEnable() || getGameManager().getState() != RaceState.INGAME) return;

        getGameManager().finish();
    }

    @Override
    public void join(Player player) {
        if(!LawerensRace.get().getGameManager().isEnable()){
            sendMessageWithPrefix(player, "EVENTO", "&cEl evento de carreras no está activado.");
            return;
        }
        if(LawerensRace.get().getGameManager().getState() != RaceState.WAITING){
            sendMessageWithPrefix(player, "EVENTO", "&c¡El evento está en juego!");
            return;
        }
        if(LawerensRace.get().getGameManager().getPlayers().size() == 30){
            sendMessageWithPrefix(player, "EVENTO", "&cEl evento ya está lleno.");
            return;
        }

        if(LawerensRace.get().getGameManager().getPlayers().contains((Player) player)){
            sendMessageWithPrefix(player, "EVENTO", "&cYa estás en el evento.");
            return;
        }

        LawerensRace.get().getGameManager().join(player);
        sendMessageWithPrefix(player, "EVENTO", "&f¡Sé bienvenido al evento!");
    }

    @Override
    public void leave(Player player) {
        if(!LawerensRace.get().getGameManager().isEnable()){
            sendMessageWithPrefix(player, "EVENTO", "&cEl evento de carreras no está activado.");
            return;
        }

        if(!LawerensRace.get().getGameManager().getPlayers().contains(player)){
            sendMessageWithPrefix(player, "EVENTO", "&cNo estás en el evento.");
            return;
        }

        getRollbacks().get(player.getUniqueId()).give(true);
        getRollbacks().remove(player.getUniqueId());

        for (Player onlinePlayer : LawerensRace.get().getGameManager().getPlayers())
            sendMessageWithPrefix(onlinePlayer, "EVENTO", "&e"+player.getName()+
                    " &fha salido. &f(&e"+LawerensRace.get().getGameManager().getPlayers().size()+"&f/&e30&f)");


        sendMessageWithPrefix(player, "EVENTO", "&fHas salido del evento Carrera.");

        if(LawerensRace.get().getGameManager().getState() == RaceState.INGAME && LawerensRace.get().getGameManager().getPlayers().isEmpty()){
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendUnderline(p, "#cf0011");
                sendMessage(p, " ");
                sendCenteredMessage(p, "&fEl Evento &cCarrera&f se detuvo");
                sendCenteredMessage(p, "&fDebido a que todos los jugadores han salido.");
                sendMessage(p, " ");
                sendUnderline(p, "#cf0011");
            }
            LawerensRace.get().getGameManager().finish();
        }
    }

    @Override
    public boolean isStarted() {
        return getGameManager().isEnable();
    }
}
