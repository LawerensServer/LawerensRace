package com.lawerens.race.listeners;

import com.lawerens.race.LawerensRace;
import com.lawerens.race.model.RaceState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static com.lawerens.race.LawerensRace.checkPlayer;
import static com.lawerens.race.utils.CommonsUtils.sendMessageWithPrefix;

public class GameListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(checkPlayer(e.getPlayer())) {
            if (LawerensRace.get().getGameManager().getState() != RaceState.INGAME) return;
            /*
            if (e.getTo().getWorld().getBlockAt(e.getTo().getBlockX(), e.getTo().getBlockY() - 1, e.getTo().getBlockZ()).getType() == LawerensRace.get().getParkourInfo().getFinishMaterial()) {
                if(e.getTo().getY() % 1 != 0) return;
                LawerensRace.get().getGameManager().win(e.getPlayer());
            }
            */

            if (e.getTo().getWorld().getBlockAt(e.getTo().getBlockX(), e.getTo().getBlockY(), e.getTo().getBlockZ()).getType() == Material.WATER) {
                e.setTo(LawerensRace.get().getRaceInfo().getStartLocation());
                e.getPlayer().playSound(e.getPlayer(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.6f);
                sendMessageWithPrefix(e.getPlayer(), "EVENTO", "&f¡Has caído, has vuelto atrás!");
            }
        }
    }

}
