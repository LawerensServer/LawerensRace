package com.lawerens.race.model;

import com.lawerens.race.LawerensRace;
import com.lawerens.race.utils.CommonsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import xyz.lawerens.utils.configuration.LawerensConfig;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;

public class RaceConfig extends LawerensConfig {
    public RaceConfig(Plugin plugin) {
        super("config", plugin.getDataFolder(), true, plugin);
    }

    public void load() {
        Bukkit.getScheduler().runTaskAsynchronously(LawerensRace.get(), () -> {
            ConfigurationSection s = asConfig();

            Location l1 = null, l2 = null;
            Material mat = null;

            if (s.getConfigurationSection("SpawnLocation") != null) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando el punto de inicio con éxito.");
                l1 = CommonsUtils.readLocation(s, "SpawnLocation");
            }

            if (s.getConfigurationSection("LobbyLocation") != null) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando el punto de Lobby con éxito.");
                l2 = CommonsUtils.readLocation(s, "LobbyLocation");
            }

            if (s.contains("FinishBlock")) {
                sendMessageWithPrefix(Bukkit.getConsoleSender(), "PARKOUR EVENTO", "&fCargando el material del fin...");
                mat = Material.matchMaterial(s.getString("FinishBlock"));
            }

            LawerensRace.get().getParkourInfo().setStartLocation(l1);
            LawerensRace.get().getParkourInfo().setLobbyLocation(l2);
            LawerensRace.get().getParkourInfo().setFinishMaterial(mat);
        });
    }


    public void save(){
        ConfigurationSection s = asConfig();

        if(LawerensRace.get().getParkourInfo().getStartLocation() != null){
            CommonsUtils.writeLocation(s, "SpawnLocation", LawerensRace.get().getParkourInfo().getStartLocation());
        }

        if(LawerensRace.get().getParkourInfo().getLobbyLocation() != null){
            CommonsUtils.writeLocation(s, "LobbyLocation", LawerensRace.get().getParkourInfo().getLobbyLocation());
        }

        if(LawerensRace.get().getParkourInfo().getFinishMaterial() != null){
            s.set("FinishBlock", LawerensRace.get().getParkourInfo().getFinishMaterial().name());
        }

        saveConfig();
    }
}
