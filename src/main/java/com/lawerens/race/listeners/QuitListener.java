package com.lawerens.race.listeners;

import com.lawerens.race.LawerensRace;
import com.lawerens.race.model.RaceState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.lawerens.race.LawerensRace.checkPlayer;
import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;
import static xyz.lawerens.utils.LawerensUtils.*;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if(LawerensRace.get().getRollbacks().containsKey(e.getPlayer().getUniqueId())){
            LawerensRace.get().getRollbacks().get(e.getPlayer().getUniqueId()).give(true);
            LawerensRace.get().getRollbacks().remove(e.getPlayer().getUniqueId());
        }

        if(checkPlayer(e.getPlayer())){
            LawerensRace.get().getGameManager().getPlayers().remove(e.getPlayer());
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
                return;
            }
            for (Player player : LawerensRace.get().getGameManager().getPlayers())
                sendMessageWithPrefix(player, "EVENTO", "&e"+e.getPlayer().getName()+
                        " &fha salido. &f(&e"+LawerensRace.get().getGameManager().getPlayers().size()+"&f/&e30&f)");

        }
    }
}
