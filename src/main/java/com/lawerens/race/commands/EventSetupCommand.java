package com.lawerens.race.commands;


import com.lawerens.events.LawerensEvents;
import com.lawerens.race.LawerensRace;
import com.lawerens.race.model.RaceCuboid;
import com.lawerens.race.model.RaceState;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;
import static xyz.lawerens.utils.LawerensUtils.filterSuggestions;
import static xyz.lawerens.utils.LawerensUtils.locationToString;

public class EventSetupCommand implements TabExecutor {

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        
        Player p = (Player) sender;
        
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("setstartlocation")) {
            LawerensRace.get().getRaceInfo().setStartLocation(p.getLocation().toCenterLocation());
            LawerensRace.get().getRaceConfig().save();
            sendMessageWithPrefix(sender, "EVENTO", "&fActualizaste la localización de inicio.");

        } else if (args[0].equalsIgnoreCase("setlobbylocation")) {
            LawerensRace.get().getRaceInfo().setLobbyLocation(p.getLocation().toCenterLocation());
            LawerensRace.get().getRaceConfig().save();
            sendMessageWithPrefix(sender, "EVENTO", "&fActualizaste la localización del lobby.");

        }
        else if(args[0].equalsIgnoreCase("enable")){
            if(LawerensRace.get().getRaceInfo().getFinishCuboid() == null || LawerensRace.get().getRaceInfo().getStartLocation() == null || LawerensRace.get().getRaceInfo().getLobbyLocation() == null){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl punto de inicio, lobby y material de meta no están definidos.");
                return false;
            }

            if(LawerensRace.get().getGameManager().getState() != RaceState.WAITING || LawerensRace.get().getGameManager().isEnable()){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl evento está activado.");
                return false;
            }
            LawerensRace.get().getGameManager().setEnable(true, sender);
            // LawerensEvents.getPlugin(LawerensEvents.class).setEvent(LawerensRace.get());
        }
        else if(args[0].equalsIgnoreCase("start")){
            if(LawerensRace.get().getGameManager().getState() != RaceState.WAITING){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl evento ya inició. &fUsa &e/lesetup stop&f para detenerlo.");
                return false;
            }
            if(LawerensRace.get().getGameManager().getPlayers().isEmpty()){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl evento no puede inciar si está vacio.");
                return false;
            }
            LawerensRace.get().getGameManager().start();
            LawerensRace.get().getGameManager().cancelTask();
        }
        else if(args[0].equalsIgnoreCase("stop")){
            if(LawerensRace.get().getGameManager().getState() != RaceState.INGAME){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl evento no ha iniciado.");
                return false;
            }
            LawerensRace.get().getGameManager().finish(sender);
        }
        else if(args[0].equalsIgnoreCase("disable")){
            if(LawerensRace.get().getGameManager().getState() != RaceState.WAITING){
                sendMessageWithPrefix(sender, "EVENTO", "&cEl evento ya inició. &fUsa &e/lesetup stop&f para detenerlo.");
                return false;
            }
            LawerensRace.get().getGameManager().setEnable(false, sender);

        }
        else if (args[0].equalsIgnoreCase("setfinishcuboid")) {

            if(pos1.get(p.getUniqueId()) == null || pos2.get(p.getUniqueId()) == null){
                sendMessageWithPrefix(sender, "EVENTO", "&cDebes seleccionar los puntos con /lcsetup pos1 y /lcsetup pos2");
                return false;
            }

            if(pos1.get(p.getUniqueId()).getWorld() != pos2.get(p.getUniqueId()).getWorld()){
                sendMessageWithPrefix(sender, "EVENTO", "&c¡Los puntos deben ser del mismo mundo!");
                return false;
            }

            LawerensRace.get().getRaceInfo().setFinishCuboid(new RaceCuboid(
                    pos1.get(p.getUniqueId()), pos2.get(p.getUniqueId())
            ));

            LawerensRace.get().getRaceConfig().save();
            sendMessageWithPrefix(sender, "EVENTO", "&fActualizaste la región de la línea de meta.");
        }
        else if(args[0].equalsIgnoreCase("addpoint")){
            if(pos1.get(p.getUniqueId()) == null || pos2.get(p.getUniqueId()) == null){
                sendMessageWithPrefix(sender, "EVENTO", "&cDebes seleccionar los puntos con /lcsetup pos1 y /lcsetup pos2");
                return false;
            }

            if(pos1.get(p.getUniqueId()).getWorld() != pos2.get(p.getUniqueId()).getWorld()){
                sendMessageWithPrefix(sender, "EVENTO", "&c¡Los puntos deben ser del mismo mundo!");
                return false;
            }

            LawerensRace.get().getRaceInfo().getPoints().add(new RaceCuboid(
                    pos1.get(p.getUniqueId()), pos2.get(p.getUniqueId())
            ));

            LawerensRace.get().getRaceConfig().save();
            sendMessageWithPrefix(sender, "EVENTO", "&fAñadiste el punto de control &e#"+LawerensRace.get().getRaceInfo().getPoints().size());
        }
        else if(args[0].equalsIgnoreCase("listpoints")){
            if(LawerensRace.get().getRaceInfo().getPoints().isEmpty()){
                sendMessageWithPrefix(sender, "EVENTO", "&cAún no hay puntos de control.");
                return false;
            }

            int i = 1;
            for (RaceCuboid point : LawerensRace.get().getRaceInfo().getPoints()) {
                sendMessageWithPrefix(sender, "EVENTO", "&e#"+i+"&7 - &f"+ locationToString(point.firstPoint())+ "&7 - &f"+locationToString(point.secondPoint()));
                i++;
            }
        }
        else if(args[0].equalsIgnoreCase("removepoint")){
            if(args.length >= 2){
                int i;

                try{
                    i = Integer.parseInt(args[1]);
                }catch (NumberFormatException e){
                    sendMessageWithPrefix(sender, "EVENTO", "&cUn número de verdad.");
                    return false;
                }

                if(i <= 0 || i > LawerensRace.get().getRaceInfo().getPoints().size()){
                    sendMessageWithPrefix(sender, "EVENTO", "&cPunto de control desconocido.");
                    return false;
                }
                i--;

                LawerensRace.get().getRaceInfo().getPoints().remove(i);
                LawerensRace.get().getRaceConfig().save();
                sendMessageWithPrefix(sender, "EVENTO", "&fEliminaste el punto de control &e#"+(i+1));

            }else{
                sendMessageWithPrefix(sender, "EVENTO", "&cElija el número del punto de control a remover.");
            }
        }
        else if(args[0].equalsIgnoreCase("pos1")){
            Location loc = p.getLocation().toBlockLocation();
            pos1.put(p.getUniqueId(), loc);
            
            sendMessageWithPrefix(sender, "EVENTO", "&fEstableciste el primer punto. &7("+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+")");
        }

        else if(args[0].equalsIgnoreCase("pos2")){
            Location loc = p.getLocation().toBlockLocation();
            pos2.put(p.getUniqueId(), loc);

            sendMessageWithPrefix(sender, "EVENTO", "&fEstableciste el segundo punto. &7("+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+")");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return filterSuggestions(
                    List.of("addpoint", "pos1", "pos2", "listpoints", "enable", "disable", "start", "stop", "setstartlocation", "setlobbylocation", "removepoint", "setfinishcuboid"), args[0]
            );
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("removepoint")){
            List<String> sug = new ArrayList<>();

            for (int i = 0; i < LawerensRace.get().getRaceInfo().getPoints().size(); i++) {
                sug.add((i+1)+"");
            }
            
            return sug;
        }
        return List.of();
    }
}
