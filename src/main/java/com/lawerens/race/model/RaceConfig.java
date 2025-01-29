package com.lawerens.race.model;

import com.lawerens.race.LawerensRace;
import com.lawerens.race.utils.CommonsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import xyz.lawerens.utils.configuration.LawerensConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;
import static xyz.lawerens.utils.LawerensUtils.locationToString;

public class RaceConfig extends LawerensConfig {

    public RaceConfig(Plugin plugin) {
        super("config", plugin.getDataFolder(), true, plugin);
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(LawerensRace.get(), () -> {
            ConfigurationSection s = asConfig();

            Location l1 = null, l2 = null;
            List<RaceCuboid> points = new ArrayList<>();
            RaceCuboid cuboid = null;

            if (s.getConfigurationSection("SpawnLocation") != null) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando el punto de inicio con éxito.");
                l1 = CommonsUtils.readLocation(s, "SpawnLocation");
            }

            if (s.getConfigurationSection("LobbyLocation") != null) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando el punto de Lobby con éxito.");
                l2 = CommonsUtils.readLocation(s, "LobbyLocation");
            }

            if (s.contains("FinishCuboid")) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando la línea de meta.");
                cuboid = new RaceCuboid(CommonsUtils.stringToLocation(s.getString("FinishCuboid").split(":")[0]),
                        CommonsUtils.stringToLocation(s.getString("FinishCuboid").split(":")[1]));
            }

            sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando puntos de control...");
            for (String str : s.getStringList("Points")) {
                points.add(
                        new RaceCuboid(
                                CommonsUtils.stringToLocation(str.split(":")[0]),
                                CommonsUtils.stringToLocation(str.split(":")[1])
                        )
                );
            }

            LawerensRace.get().getRaceInfo().setStartLocation(l1);
            LawerensRace.get().getRaceInfo().setLobbyLocation(l2);
            LawerensRace.get().getRaceInfo().setPoints(points);
            LawerensRace.get().getRaceInfo().setFinishCuboid(cuboid);
        });
    }


    public void save(){
        ConfigurationSection s = asConfig();

        if(LawerensRace.get().getRaceInfo().getStartLocation() != null){
            CommonsUtils.writeLocation(s, "SpawnLocation", LawerensRace.get().getRaceInfo().getStartLocation());
        }

        if(LawerensRace.get().getRaceInfo().getLobbyLocation() != null){
            CommonsUtils.writeLocation(s, "LobbyLocation", LawerensRace.get().getRaceInfo().getLobbyLocation());
        }

        if(LawerensRace.get().getRaceInfo().getFinishCuboid() != null){
            s.set("FinishCuboid", locationToString(LawerensRace.get().getRaceInfo().getFinishCuboid().firstPoint())+":"+locationToString(LawerensRace.get().getRaceInfo().getFinishCuboid().secondPoint()));
        }

        List<String> points = new ArrayList<>();
        for (RaceCuboid point : LawerensRace.get().getRaceInfo().getPoints()) {
            points.add(locationToString(point.firstPoint())+":"+locationToString(point.secondPoint()));
        }

        s.set("Points", points);

        saveConfig();
    }
}
